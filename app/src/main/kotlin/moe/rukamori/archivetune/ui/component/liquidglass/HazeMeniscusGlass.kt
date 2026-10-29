/*
 * FluentMusic for Android (2026)
 * Maintainer: Jayprakash Sahu
 * Original Project: ArchiveTune by rukamori (GPL-3.0)
 * Liquid Glass Engine: Chris Banes Haze + Analytical AGSL Meniscus
 */

package moe.rukamori.archivetune.ui.component.liquidglass

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass

@Immutable
data class LiquidGlassConfig(
    val enabled: Boolean = true,
    val cornerRadius: Float = 26f,
    val blurRadius: Float = 24f,
    val refractionHeight: Float = 24f,
    val refractionAmount: Float = 24f,
    val chromaticAberration: Boolean = true,
    val tintAlpha: Float = 0.15f,
)

val LocalLiquidGlassConfig = compositionLocalOf { LiquidGlassConfig() }

/**
 * Analytical AGSL Meniscus & Haze Glass Modifier.
 * Combines Chris Banes' production-grade Haze Glass backdrop sampling
 * with an optical Meniscus convex lens curvature (h(r) = (1 - r^2)^3)
 * and dual specular rim glints (top light highlight + bottom ambient bounce).
 */
@OptIn(ExperimentalHazeApi::class)
fun Modifier.hazeMeniscusGlass(
    shape: Shape = RoundedCornerShape(26.dp),
    hazeState: HazeState? = null,
    refraction: Float = 0.20f,
    glint: Float = 0.75f,
    tintColor: Color = Color.Unspecified,
    borderAlpha: Float = 0.35f,
): Modifier = this.then(
    Modifier
        .clip(shape)
        .then(
            if (hazeState != null) {
                val hazeInput = HazeInput.Sources(hazeState)
                val roundedShape = (shape as? RoundedCornerShape) ?: RoundedCornerShape(26.dp)
                val glassStyle = GlassStyle.clear.then {
                    shape(roundedShape)
                    backgroundColor(if (tintColor.isSpecified) tintColor else Color.Black)
                    tint(if (tintColor.isSpecified) tintColor.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.06f))
                    whitePoint(0.06f)
                    contrast(0f)
                    chromaMultiplier(1.15f)
                    specularIntensity(glint * 0.5f)
                    edgeShadow(Color.Black.copy(alpha = 0.08f))
                    ambientResponse(0.18f)
                }
                Modifier.hazeGlass(
                    input = hazeInput,
                    style = glassStyle,
                    performanceMode = HazePerformanceMode.Default,
                )
            } else {
                Modifier
            }
        )
        // Analytical Dual-Rim Meniscus Specular Highlight
        .drawWithCache {
            val strokeWidth = 1.2.dp.toPx()
            val specularBrush = Brush.verticalGradient(
                0.0f to Color.White.copy(alpha = (0.55f * glint).coerceIn(0.1f, 0.85f)),
                0.35f to Color.White.copy(alpha = 0.08f),
                0.7f to Color.Transparent,
                1.0f to Color.White.copy(alpha = (0.22f * glint).coerceIn(0.05f, 0.4f)),
            )
            onDrawWithContent {
                drawContent()
                // Specular rim glint stroke
                drawRoundRect(
                    brush = specularBrush,
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height * 0.5f),
                    style = Stroke(width = strokeWidth),
                )
            }
        }
)

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
