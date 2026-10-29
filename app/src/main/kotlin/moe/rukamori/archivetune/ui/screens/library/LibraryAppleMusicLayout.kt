/*
 * FluentMusic for Android (2026)
 * Maintainer: Jayprakash Sahu
 * Original Project: ArchiveTune by rukamori (GPL-3.0)
 * iOS 26 / Apple Music Library Components
 */

package moe.rukamori.archivetune.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import moe.rukamori.archivetune.R
import moe.rukamori.archivetune.constants.LibraryFilter
import moe.rukamori.archivetune.models.MediaMetadata
import moe.rukamori.archivetune.ui.component.liquidglass.drizlLiquidGlass

private val AppleAccentRed = Color(0xFFFA2D48)

data class LibraryGridItem(
    val title: String,
    val subtitle: String = "",
    val thumbnailUrl: String?,
    val onClick: () -> Unit,
)

/**
 * The 6-Grid Quick Access section (3 columns x 2 rows) matching iOS 26 Apple Music Library.
 */
@Composable
fun LibrarySixGridSection(
    items: List<LibraryGridItem>,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return

    val displayItems = items.take(6)
    val row1 = displayItems.take(3)
    val row2 = displayItems.drop(3).take(3)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            row1.forEach { item ->
                LibraryGridTile(
                    item = item,
                    modifier = Modifier.weight(1f),
                )
            }
            // Fill empty spaces if less than 3
            repeat(3 - row1.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        if (row2.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                row2.forEach { item ->
                    LibraryGridTile(
                        item = item,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - row2.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LibraryGridTile(
    item: LibraryGridItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = item.onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .drizlLiquidGlass(
                    shape = RoundedCornerShape(14.dp),
                    refraction = 0.12f,
                    glint = 0.5f,
                    borderAlpha = 0.25f,
                ),
        ) {
            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            ),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/**
 * iOS Apple Music Style Category Navigation Rows (Playlists, Artists, Albums, Songs).
 */
@Composable
fun LibraryCategoryNavigationList(
    onCategoryClick: (LibraryFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        LibraryCategoryRow(
            title = stringResource(R.string.playlists),
            iconRes = R.drawable.queue_music,
            onClick = { onCategoryClick(LibraryFilter.PLAYLISTS) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 38.dp),
        )
        LibraryCategoryRow(
            title = stringResource(R.string.artists),
            iconRes = R.drawable.artist,
            onClick = { onCategoryClick(LibraryFilter.ARTISTS) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 38.dp),
        )
        LibraryCategoryRow(
            title = stringResource(R.string.albums),
            iconRes = R.drawable.album,
            onClick = { onCategoryClick(LibraryFilter.ALBUMS) },
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 38.dp),
        )
        LibraryCategoryRow(
            title = stringResource(R.string.songs),
            iconRes = R.drawable.music_note,
            onClick = { onCategoryClick(LibraryFilter.SONGS) },
        )
    }
}

@Composable
private fun LibraryCategoryRow(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = AppleAccentRed,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.navigate_next),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp),
        )
    }
}
