/*
 * FluentMusic for Android (2026)
 * Maintainer: Jayprakash Sahu
 * Original Project: ArchiveTune by rukamori (GPL-3.0)
 * Liquid Glass Engine: AndroidLiquidGlass by Kyant0 (Apache-2.0)
 */

package moe.rukamori.archivetune.ui.component.liquidglass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Immutable
data class LiquidGlassConfig(
    val enabled: Boolean = true,
    val cornerRadius: Float = 16f,
    val blurRadius: Float = 16f,
    val refractionHeight: Float = 24f,
    val refractionAmount: Float = 24f,
    val chromaticAberration: Boolean = true,
    val tintAlpha: Float = 0.22f,
)

val LocalLiquidGlassConfig = compositionLocalOf { LiquidGlassConfig() }
val LocalBackdrop = compositionLocalOf<Backdrop?> { null }

/**
 * Applies the Liquid Glass backdrop shader effect if enabled, or a modern frosted glass fallback.
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(16.dp),
    backdrop: Backdrop? = null,
    config: LiquidGlassConfig = LiquidGlassConfig(),
    tintColor: Color = Color.Unspecified,
    borderAlpha: Float = 0.35f,
): Modifier {
    if (!config.enabled) {
        return this
    }

    return this.then(
        Modifier
            .clip(shape)
            .then(
                if (backdrop != null) {
                    Modifier.drawBackdrop(
                        backdrop = backdrop,
                        shape = { shape },
                        effects = {
                            vibrancy()
                            val blurPx = (config.blurRadius * 2.5f).coerceAtLeast(0.1f)
                            val lensHPx = (config.refractionHeight * 1.5f).coerceAtLeast(0.1f)
                            val lensAPx = (config.refractionAmount * 1.5f).coerceAtLeast(0.1f)
                            blur(blurPx)
                            lens(lensHPx, lensAPx, chromaticAberration = config.chromaticAberration)
                        },
                    )
                } else {
                    Modifier
                },
            )
            .background(
                color = if (tintColor.isSpecified) {
                    tintColor.copy(alpha = config.tintAlpha)
                } else {
                    Color.White.copy(alpha = config.tintAlpha * 0.5f)
                },
                shape = shape,
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = borderAlpha),
                        Color.White.copy(alpha = borderAlpha * 0.2f),
                    ),
                ),
                shape = shape,
            ),
    )
}

/**
 * A capsule container applying authentic liquid glass effects, ideal for buttons, play buttons, and chips.
 */
@Composable
fun LiquidGlassCapsule(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    tintColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit,
) {
    val config = LocalLiquidGlassConfig.current
    val backdrop = LocalBackdrop.current
    val colorScheme = MaterialTheme.colorScheme
    val resolvedTintColor = if (tintColor.isSpecified) tintColor else colorScheme.surfaceVariant

    Box(
        modifier = modifier.liquidGlass(
            shape = shape,
            backdrop = backdrop,
            config = config,
            tintColor = resolvedTintColor,
        ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/**
 * Progressive blur backdrop header container for scrollable pages (Home, Settings, Playlist, Artist).
 * Floats smoothly above scrolled list items with Liquid Glass blur and refraction.
 */
@Composable
fun LiquidGlassHeaderContainer(
    scrollProgress: Float,
    modifier: Modifier = Modifier,
    backdrop: Backdrop? = LocalBackdrop.current,
    config: LiquidGlassConfig = LocalLiquidGlassConfig.current,
    content: @Composable BoxScope.() -> Unit,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = scrollProgress.coerceIn(0f, 1f),
        label = "headerProgress",
    )
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.surface.luminance() < 0.5f

    val headerTint = remember(animatedProgress, isDark, config.tintAlpha) {
        val baseAlpha = if (animatedProgress > 0.05f) {
            (config.tintAlpha + (animatedProgress * 0.45f)).coerceIn(0f, 0.85f)
        } else {
            0f
        }
        if (isDark) {
            Color.Black.copy(alpha = baseAlpha)
        } else {
            Color.White.copy(alpha = baseAlpha)
        }
    }

    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .then(
                if (config.enabled && animatedProgress > 0.05f && backdrop != null) {
                    Modifier.drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp) },
                        effects = {
                            vibrancy()
                            val dynamicBlur = (config.blurRadius * animatedProgress * 2f).coerceAtLeast(0.1f)
                            blur(dynamicBlur)
                            lens(dynamicBlur * 0.5f, dynamicBlur, chromaticAberration = config.chromaticAberration)
                        },
                    )
                } else {
                    Modifier
                },
            )
            .background(headerTint)
            .then(
                if (animatedProgress > 0.1f) {
                    Modifier.border(
                        width = 0.5.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                if (isDark) Color.White.copy(alpha = 0.12f * animatedProgress)
                                else Color.Black.copy(alpha = 0.08f * animatedProgress),
                            ),
                        ),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    )
                } else {
                    Modifier
                },
            ),
        content = content,
    )
}
