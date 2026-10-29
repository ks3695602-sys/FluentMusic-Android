/*
 * ArchiveTune (2026)
 * © Rukamori — github.com/rukamori
 * GPL-3.0 License | Contributors: see git history
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 * Liquid Glass Dock: DrizL Inc. Engine (Ported to AGSL)
 */

package moe.rukamori.archivetune.ui.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import moe.rukamori.archivetune.LocalAnimationsDisabled
import moe.rukamori.archivetune.constants.NavigationBarHeight
import moe.rukamori.archivetune.constants.NavigationBarMaxWidth
import moe.rukamori.archivetune.ui.component.liquidglass.LocalLiquidGlassConfig
import moe.rukamori.archivetune.ui.component.liquidglass.hazeMeniscusGlass
import moe.rukamori.archivetune.ui.screens.Screens

private val AppleRedAccent = Color(0xFFFA2D48)

@Composable
internal fun AppleMusicNavigationToolbar(
    items: List<Screens>,
    pureBlack: Boolean,
    hazeState: HazeState,
    modifier: Modifier,
    isSelected: (Screens) -> Boolean,
    onItemClick: (Screens, Boolean) -> Unit,
    onSearchItemDoubleClick: (() -> Unit)?,
) {
    val groupedItems = remember(items) { items.filterNot { it == Screens.Search } }
    val hasSearch = Screens.Search in items
    val shape = remember { RoundedCornerShape(percent = 50) }
    val selectedIndex = groupedItems.indexOfFirst(isSelected)
    val animationsDisabled = LocalAnimationsDisabled.current
    val isLiquidGlassEnabled = LocalLiquidGlassConfig.current.enabled

    val indicatorPosition = animateFloatAsState(
        targetValue = selectedIndex.coerceAtLeast(0).toFloat(),
        animationSpec = if (animationsDisabled) snap() else spring(
            dampingRatio = 0.75f,
            stiffness = Spring.StiffnessLow,
        ),
        label = "appleMusicSelectionPosition",
    )
    val indicatorAlpha = animateFloatAsState(
        targetValue = if (selectedIndex >= 0) 1f else 0f,
        animationSpec = if (animationsDisabled) snap() else spring(
            dampingRatio = 0.75f,
            stiffness = Spring.StiffnessLow,
        ),
        label = "appleMusicSelectionAlpha",
    )

    val activeIndicatorColor = AppleRedAccent.copy(alpha = 0.16f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = NavigationBarMaxWidth)
                .fillMaxWidth()
                .height(NavigationBarHeight)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Main Navigation Pill
            if (groupedItems.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .hazeMeniscusGlass(
                            shape = shape,
                            hazeState = hazeState,
                            tintColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainerHigh,
                            glint = 0.8f,
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .drawWithCache {
                                val itemWidth = size.width / groupedItems.size
                                val radius = CornerRadius(size.height / 2f)
                                onDrawBehind {
                                    val position = indicatorPosition.value.coerceIn(0f, groupedItems.lastIndex.toFloat())
                                    val left = if (layoutDirection == LayoutDirection.Ltr) {
                                        position * itemWidth
                                    } else {
                                        size.width - (position + 1f) * itemWidth
                                    }
                                    // Fluid indicator pill background
                                    drawRoundRect(
                                        color = activeIndicatorColor,
                                        topLeft = Offset(left + 2f, 2f),
                                        size = Size(itemWidth - 4f, size.height - 4f),
                                        cornerRadius = radius,
                                        alpha = indicatorAlpha.value,
                                    )
                                }
                            },
                    ) {
                        groupedItems.forEach { screen ->
                            key(screen.route) {
                                AppleMusicNavigationItem(
                                    screen = screen,
                                    selected = isSelected(screen),
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    onItemClick = onItemClick,
                                    onSearchItemDoubleClick = onSearchItemDoubleClick,
                                )
                            }
                        }
                    }
                }
            }

            // Standalone Circular Search Lens Bubble
            if (hasSearch) {
                Box(
                    modifier = Modifier
                        .size(NavigationBarHeight)
                        .hazeMeniscusGlass(
                            shape = CircleShape,
                            hazeState = hazeState,
                            tintColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainerHigh,
                            glint = 0.85f,
                        ),
                ) {
                    AppleMusicNavigationItem(
                        screen = Screens.Search,
                        selected = isSelected(Screens.Search),
                        modifier = Modifier.fillMaxSize(),
                        isSearchCircle = true,
                        onItemClick = onItemClick,
                        onSearchItemDoubleClick = onSearchItemDoubleClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppleMusicNavigationItem(
    screen: Screens,
    selected: Boolean,
    modifier: Modifier,
    isSearchCircle: Boolean = false,
    onItemClick: (Screens, Boolean) -> Unit,
    onSearchItemDoubleClick: (() -> Unit)?,
) {
    val animationsDisabled = LocalAnimationsDisabled.current
    val tint = animateColorAsState(
        targetValue = if (selected) AppleRedAccent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        animationSpec = if (animationsDisabled) snap() else spring(
            dampingRatio = 0.75f,
            stiffness = Spring.StiffnessLow,
        ),
        label = "appleMusicItemTint",
    )
    val scale = animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = if (animationsDisabled) snap() else spring(
            dampingRatio = 0.75f,
            stiffness = Spring.StiffnessLow,
        ),
        label = "appleMusicItemScale",
    )
    val shape = remember { RoundedCornerShape(percent = 50) }
    val onClick = rememberNavigationItemClick(screen, selected, onItemClick, onSearchItemDoubleClick)
    val title = stringResource(screen.titleId)

    Box(
        modifier = modifier
            .clip(shape)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .semantics { contentDescription = title },
        contentAlignment = Alignment.Center,
    ) {
        if (isSearchCircle) {
            Icon(
                painter = painterResource(if (selected) screen.iconIdActive else screen.iconIdInactive),
                contentDescription = null,
                tint = tint.value,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    },
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 2.dp),
            ) {
                Icon(
                    painter = painterResource(if (selected) screen.iconIdActive else screen.iconIdInactive),
                    contentDescription = null,
                    tint = tint.value,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                        },
                )
                Text(
                    text = title,
                    maxLines = 1,
                    fontSize = 10.sp,
                    color = tint.value,
                )
            }
        }
    }
}
