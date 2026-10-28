/*
 * FluentMusic for Android (2026)
 * Maintainer: Jayprakash Sahu
 * Original Project: ArchiveTune by rukamori (GPL-3.0)
 * Liquid Glass Engine: AndroidLiquidGlass by Kyant0 (Apache-2.0)
 */

package moe.rukamori.archivetune.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import moe.rukamori.archivetune.ui.component.liquidglass.LocalBackdrop
import moe.rukamori.archivetune.ui.component.liquidglass.LocalLiquidGlassConfig
import moe.rukamori.archivetune.ui.component.liquidglass.liquidGlass
import moe.rukamori.archivetune.ui.component.liquidglass.LiquidButton

@Composable
fun ResizableIconButton(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true,
    indication: Indication? = null,
    onClick: () -> Unit = {},
) {
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color),
        modifier =
            modifier
                .clickable(
                    indication = indication ?: ripple(bounded = false),
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = enabled,
                    onClick = onClick,
                ).alpha(if (enabled) 1f else 0.5f),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconButton(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val config = LocalLiquidGlassConfig.current

    if (config.enabled) {
        val buttonTint = if (colors.containerColor != Color.Transparent) {
            colors.containerColor.copy(alpha = 0.28f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f)
        }
        LiquidButton(
            onClick = onClick,
            onLongClick = onLongClick,
            shape = CircleShape,
            isInteractive = enabled,
            tint = buttonTint,
            surfaceColor = Color.White.copy(alpha = 0.05f),
            height = 48.dp,
            modifier = modifier.minimumInteractiveComponentSize().sizeIn(minWidth = 48.dp, minHeight = 48.dp),
        ) {
            val contentColor = colors.contentColor
            CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
        }
    } else {
        Box(
            modifier =
                modifier
                    .minimumInteractiveComponentSize()
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .clip(CircleShape)
                    .background(color = colors.containerColor)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick,
                        enabled = enabled,
                        role = Role.Button,
                        interactionSource = interactionSource,
                        indication =
                            ripple(
                                bounded = false,
                                radius = 24.dp,
                            ),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            val contentColor = colors.contentColor
            CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
        }
    }
}
