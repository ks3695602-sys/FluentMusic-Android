/*
 * FluentMusic for Android (2026)
 * Maintainer: Jayprakash Sahu
 * Original Project: ArchiveTune by rukamori (GPL-3.0)
 * Apple Music Live Radio & Endless Station Engine
 */

package moe.rukamori.archivetune.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import moe.rukamori.archivetune.LocalDatabase
import moe.rukamori.archivetune.LocalPlayerAwareWindowInsets
import moe.rukamori.archivetune.LocalPlayerConnection
import moe.rukamori.archivetune.R
import moe.rukamori.archivetune.innertube.models.WatchEndpoint
import moe.rukamori.archivetune.models.toMediaMetadata
import moe.rukamori.archivetune.playback.queues.YouTubeQueue
import moe.rukamori.archivetune.ui.component.liquidglass.hazeMeniscusGlass

private val AppleRed = Color(0xFFFA2D48)

private data class LiveStation(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val seedVideoId: String,
    val gradientColors: List<Color>,
)

private val CuratedStations = listOf(
    LiveStation(
        id = "am1",
        title = "Apple Music 1",
        subtitle = "The new music that matters today, live interviews and culture.",
        category = "LIVE GLOBAL BROADCAST",
        seedVideoId = "kJQP7kiw5Fk",
        gradientColors = listOf(Color(0xFFFA2D48), Color(0xFF5E00C8)),
    ),
    LiveStation(
        id = "am_hits",
        title = "Apple Music Hits",
        subtitle = "Songs you know and love from the '80s, '90s, and 2000s.",
        category = "TIMELESS CLASSICS",
        seedVideoId = "fJ9rUzIMcZQ",
        gradientColors = listOf(Color(0xFF007AFF), Color(0xFF00C6FF)),
    ),
    LiveStation(
        id = "chill_waves",
        title = "Chill Waves Radio",
        subtitle = "Lo-fi beats, warm synthesizers, and relaxed evening grooves.",
        category = "ENDLESS AMBIENT",
        seedVideoId = "jfKfPfyJRdk",
        gradientColors = listOf(Color(0xFF4A00E0), Color(0xFF8E2DE2)),
    ),
    LiveStation(
        id = "deep_focus",
        title = "Deep Focus Flow",
        subtitle = "Zero vocals, pure hypnotic flow state for coding and study.",
        category = "PRODUCTIVITY",
        seedVideoId = "DWcJFNfaw9c",
        gradientColors = listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
    ),
    LiveStation(
        id = "hiphop_pulse",
        title = "The Beat Pulse",
        subtitle = "Heavy 808s, fresh drops, and boundary-pushing hip-hop.",
        category = "HIGH ENERGY",
        seedVideoId = "hHW1oY26kxQ",
        gradientColors = listOf(Color(0xFFFF416C), Color(0xFFFF4B2B)),
    ),
)

@Composable
fun RadioScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val database = LocalDatabase.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val recentSongs by database.recentSongs(12).collectAsState(initial = emptyList())
    val allArtists by database.artistsByPlayTimeAsc().collectAsState(initial = emptyList())
    val topArtists = remember(allArtists) { allArtists.reversed().take(10) }

    val insets = LocalPlayerAwareWindowInsets.current.asPaddingValues()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = insets.calculateTopPadding() + 8.dp,
            bottom = insets.calculateBottomPadding() + 80.dp,
        ),
    ) {
        // Apple Music Large Header
        item(key = "radio_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.radio),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp,
                            letterSpacing = (-0.5).sp,
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Continuous Live Stations & Endless Mixes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Live On-Air Pill Badge
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = AppleRed.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppleRed.copy(alpha = 0.35f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(AppleRed),
                        )
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                            ),
                            color = AppleRed,
                        )
                    }
                }
            }
        }

        // Featured Live Stations Hero Carousel
        item(key = "featured_stations_title") {
            Text(
                text = "Featured Live Stations",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        item(key = "featured_stations_carousel") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(CuratedStations, key = { it.id }) { station ->
                    LiveStationHeroCard(
                        station = station,
                        onPlay = {
                            val queue = YouTubeQueue(
                                endpoint = WatchEndpoint(
                                    videoId = station.seedVideoId,
                                    playlistId = "RDAMVM${station.seedVideoId}",
                                ),
                                followAutomixPreview = true,
                            )
                            playerConnection.playQueue(queue)
                        },
                    )
                }
            }
        }

        // Endless Artist Radio Stations
        if (topArtists.isNotEmpty()) {
            item(key = "artist_stations_title") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Endless Artist Stations",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }

            item(key = "artist_stations_row") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(topArtists, key = { it.id }) { artist ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(96.dp)
                                .clickable {
                                    val queue = YouTubeQueue(
                                        endpoint = WatchEndpoint(
                                            playlistId = "RDEM${artist.id}",
                                        ),
                                        followAutomixPreview = true,
                                    )
                                    playerConnection.playQueue(queue)
                                },
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                AsyncImage(
                                    model = artist.artist.thumbnailUrl,
                                    contentDescription = artist.artist.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                                // Ambient Station Overlay Icon
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(AppleRed),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.radio),
                                        contentDescription = "Start Artist Radio",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp),
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = artist.artist.name,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            }
        }

        // Quick Infinite Radio from Songs
        if (recentSongs.isNotEmpty()) {
            item(key = "song_radios_title") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Start Radio from Tracks",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }

            items(recentSongs.take(6), key = { it.id }) { song ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val queue = YouTubeQueue.radio(song.toMediaMetadata())
                            playerConnection.playQueue(queue)
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        AsyncImage(
                            model = song.song.thumbnailUrl,
                            contentDescription = song.song.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.song.title,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "Infinite Station • ${song.artists.joinToString { it.name }}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier.size(36.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.radio),
                                contentDescription = "Start Radio",
                                tint = AppleRed,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveStationHeroCard(
    station: LiveStation,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(280.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(station.gradientColors)
            )
            .clickable(onClick = onPlay)
            .padding(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = station.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    ),
                    color = Color.White.copy(alpha = 0.82f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = station.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = Color.White,
                )
            }

            Column {
                Text(
                    text = station.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Listen Now",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.play),
                            contentDescription = "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}
