/*
 * FluentMusic for Android (2026)
 * Streaming Engine: Adapted from Echo-Music (GPL-3.0)
 * Line-rate bounded range streaming to eliminate 15 kB/s server pacing.
 */

package moe.rukamori.archivetune.playback

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import moe.rukamori.archivetune.utils.PlayerClient
import timber.log.Timber
import java.io.InterruptedIOException

/**
 * Fetches a stream as a run of bounded ranges instead of one open-ended read.
 *
 * googlevideo paces a continuous response down to roughly playback speed: ask for a whole track in
 * one request and it arrives at about 15kB/s, barely ahead of consumption rate. Ask for the same
 * bytes as bounded ranges (e.g. 1MB or 512KB) and each one is served at full line rate (~5.7+ MB/s).
 *
 * Transparent to CacheDataSource above it, ensuring smooth instant starts without stalling.
 */
@UnstableApi
class ChunkedDataSource(
    private val upstream: DataSource,
    private val chunkBytes: Long = 1024L * 1024L,
) : DataSource {

    private var baseSpec: DataSpec? = null
    private var position = 0L
    private var bytesRemaining = 0L
    private var chunkRemaining = 0L
    private var chunkOpen = false
    private var rangeBytes = chunkBytes
    private var passthrough = false

    override fun addTransferListener(transferListener: TransferListener) {
        upstream.addTransferListener(transferListener)
    }

    override fun open(dataSpec: DataSpec): Long {
        baseSpec = dataSpec
        position = dataSpec.position

        val total = dataSpec.uri.getQueryParameter("clen")?.toLongOrNull()
        if (total == null) {
            passthrough = true
            chunkOpen = true
            return try {
                upstream.open(dataSpec)
            } catch (e: Exception) {
                if (e !is InterruptedIOException) {
                    val who = dataSpec.uri.host ?: dataSpec.uri.toString().take(120)
                    Timber.tag(TAG).w("$who refused the stream: ${e.message}")
                }
                throw e
            }
        }

        passthrough = false
        val end =
            if (dataSpec.length == C.LENGTH_UNSET.toLong()) {
                total
            } else {
                minOf(total, position + dataSpec.length)
            }
        bytesRemaining = (end - position).coerceAtLeast(0L)
        rangeBytes = minOf(chunkBytes, PlayerClient.rangeBytesFor(dataSpec.uri.toString()))
        if (bytesRemaining > 0) openChunk()
        return bytesRemaining
    }

    private fun openChunk() {
        val length = minOf(rangeBytes, bytesRemaining)
        val spec = requireNotNull(baseSpec).buildUpon().setPosition(position).setLength(length).build()
        try {
            upstream.open(spec)
        } catch (e: Exception) {
            if (e !is InterruptedIOException) {
                Timber.tag(TAG).w(
                    "range $position-${position + length - 1} refused for " +
                        "${spec.uri.getQueryParameter("c")}: ${e.message}",
                )
            }
            throw e
        }
        chunkRemaining = length
        chunkOpen = true
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (passthrough) return upstream.read(buffer, offset, length)
        if (bytesRemaining == 0L) return C.RESULT_END_OF_INPUT

        repeat(MAX_EMPTY_RANGES) {
            if (chunkRemaining == 0L) {
                closeChunk()
                openChunk()
            }
            val read = upstream.read(buffer, offset, minOf(length.toLong(), chunkRemaining).toInt())
            if (read != C.RESULT_END_OF_INPUT) {
                position += read
                chunkRemaining -= read
                bytesRemaining -= read
                return read
            }
            chunkRemaining = 0L
        }
        return C.RESULT_END_OF_INPUT
    }

    private fun closeChunk() {
        if (chunkOpen) {
            upstream.close()
            chunkOpen = false
        }
    }

    override fun getUri(): Uri? = upstream.uri ?: baseSpec?.uri

    override fun getResponseHeaders(): Map<String, List<String>> = upstream.responseHeaders

    override fun close() {
        closeChunk()
        baseSpec = null
        bytesRemaining = 0L
        chunkRemaining = 0L
    }

    private companion object {
        const val TAG = "FluentChunkedDataSource"
        const val MAX_EMPTY_RANGES = 3
    }

    class Factory(
        private val upstream: DataSource.Factory,
        private val chunkBytes: Long = 1024L * 1024L,
    ) : DataSource.Factory {
        override fun createDataSource(): DataSource =
            ChunkedDataSource(upstream.createDataSource(), chunkBytes)
    }
}
