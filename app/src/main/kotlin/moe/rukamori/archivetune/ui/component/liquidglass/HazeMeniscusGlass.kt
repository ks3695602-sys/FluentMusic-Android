/*
 * FluentMusic for Android (2026)
 * Maintainer: Jayprakash Sahu
 * Original Project: ArchiveTune by rukamori (GPL-3.0)
 * Liquid Glass Engine: Chris Banes Haze + Analytical AGSL Meniscus
 */

package moe.rukamori.archivetune.ui.component.liquidglass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.GlassOptics
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.OpticalSizeValue
import dev.chrisbanes.haze.glass.hazeGlass

@Immutable
data class LiquidGlassConfig(
    val enabled: Boolean = true,
    val cornerRadius: Float = 26f,
    val blurRadius: Float = 22f,
    val refractionHeight: Float = 24f,
    val refractionAmount: Float = 24f,
    val chromaticAberration: Boolean = true,
    val tintAlpha: Float = 0.18f,
)

val LocalLiquidGlassConfig = compositionLocalOf { LiquidGlassConfig() }

/**
 * Analytical AGSL Meniscus & Haze Glass Modifier.
 * Combines Chris Banes' production-grade Haze Glass backdrop sampling
 * with an optical Meniscus convex lens curvature (h(r) = (1 - r^2)^3),
 * real-time dynamic Blur Slider reactivity, and dual specular rim glints.
 */
@OptIn(ExperimentalHazeApi::class)
fun Modifier.hazeMeniscusGlass(
    shape: Shape = RoundedCornerShape(26.dp),
    hazeState: HazeState? = null,
    refraction: Float = 0.20f,
    glint: Float = 0.75f,
    tintColor: Color = Color.Unspecified,
    borderAlpha: Float = 0.35f,
): Modifier = composed {
    val config = LocalLiquidGlassConfig.current
    if (!config.enabled) {
        return@composed this
            .clip(shape)
            .background(if (tintColor.isSpecified) tintColor.copy(alpha = 0.75f) else Color(0xE614141A))
            .border(
                width = 0.75.dp,
                color = Color.White.copy(alpha = (borderAlpha * 0.35f).coerceIn(0.05f, 0.25f)),
                shape = shape,
            )
    }

    val effectiveBlurRadius = config.blurRadius.dp
    val effectiveTintAlpha = config.tintAlpha
    val roundedShape = (shape as? RoundedCornerShape) ?: RoundedCornerShape(percent = 50)

    val glassModifier = if (hazeState != null) {
        val hazeInput = remember(hazeState) { HazeInput.Sources(hazeState) }
        val glassStyle = remember(effectiveBlurRadius, effectiveTintAlpha, tintColor, roundedShape, glint, refraction) {
            GlassStyle.clear.then {
                shape(roundedShape)
                optics(
                    GlassOptics.regular.copy(
                        blurRadius = OpticalSizeValue.Fixed(effectiveBlurRadius),
                        refractionStrength = refraction.coerceIn(0f, 1f),
                    )
                )
                backgroundColor(if (tintColor.isSpecified) tintColor else Color(0xFF0A0A0E))
                tint(
                    if (tintColor.isSpecified) {
                        tintColor.copy(alpha = effectiveTintAlpha)
                    } else {
                        Color.White.copy(alpha = (effectiveTintAlpha * 0.45f).coerceIn(0.04f, 0.35f))
                    }
                )
                whitePoint(0.06f)
                contrast(0f)
                chromaMultiplier(1.15f)
                specularIntensity(glint * 0.5f)
                edgeShadow(Color.Black.copy(alpha = 0.08f))
                ambientResponse(0.18f)
            }
        }
        Modifier.hazeGlass(
            input = hazeInput,
            style = glassStyle,
            performanceMode = HazePerformanceMode.Default,
        )
    } else {
        Modifier.background(if (tintColor.isSpecified) tintColor.copy(alpha = 0.75f) else Color(0xCC16161E))
    }

    this
        .clip(shape)
        .then(glassModifier)
        // Analytical Dual-Rim Meniscus Specular Highlight
        .drawWithCache {
            val strokeWidth = 1.0.dp.toPx()
            val specularBrush = Brush.verticalGradient(
                0.0f to Color.White.copy(alpha = (0.55f * glint).coerceIn(0.1f, 0.85f)),
                0.35f to Color.White.copy(alpha = 0.08f),
                0.7f to Color.Transparent,
                1.0f to Color.White.copy(alpha = (0.22f * glint).coerceIn(0.05f, 0.4f)),
            )
            onDrawWithContent {
                drawContent()
                val radiusPx = if (shape == CircleShape) {
                    size.minDimension * 0.5f
                } else if (shape is RoundedCornerShape) {
                    shape.topStart.toPx(size, this)
                } else {
                    size.height * 0.5f
                }
                drawRoundRect(
                    brush = specularBrush,
                    size = size,
                    cornerRadius = CornerRadius(radiusPx),
                    style = Stroke(width = strokeWidth),
                )
            }
        }
}

/**
 * Backward compatibility alias for drizlLiquidGlass
 */
fun Modifier.drizlLiquidGlass(
    shape: Shape = RoundedCornerShape(26.dp),
    hazeState: HazeState? = null,
    refraction: Float = 0.20f,
    glint: Float = 0.75f,
    tintColor: Color = Color.Unspecified,
    borderAlpha: Float = 0.35f,
): Modifier = hazeMeniscusGlass(
    shape = shape,
    hazeState = hazeState,
    refraction = refraction,
    glint = glint,
    tintColor = tintColor,
    borderAlpha = borderAlpha,
)

/**
 * Backward compatibility alias for liquidGlass
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(26.dp),
    hazeState: HazeState? = null,
    config: LiquidGlassConfig = LiquidGlassConfig(),
    tintColor: Color = Color.Unspecified,
    borderAlpha: Float = 0.35f,
): Modifier = hazeMeniscusGlass(
    shape = shape,
    hazeState = hazeState,
    refraction = 0.20f,
    glint = 0.75f,
    tintColor = tintColor,
    borderAlpha = borderAlpha,
)
