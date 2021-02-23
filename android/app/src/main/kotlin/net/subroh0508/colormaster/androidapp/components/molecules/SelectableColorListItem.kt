package net.subroh0508.colormaster.androidapp.components.molecules

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.components.atoms.ColorItemContent
import net.subroh0508.colormaster.androidapp.themes.darkBackground
import net.subroh0508.colormaster.androidapp.themes.hexToColor
import net.subroh0508.colormaster.androidapp.themes.lightBackground
import net.subroh0508.colormaster.model.HexColor

@ExperimentalFoundationApi
@Composable
fun SelectableColorListItem(
    label: String,
    color: HexColor,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    favorited: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onClickFavorite: () -> Unit = {},
) {
    Card(
        elevation = 4.dp,
        backgroundColor = color.hexToColor(),
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
        ),
    ) {
        Box(modifier.fillMaxWidth()) {
            if (selected) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = if (color.isBrighter) Color.Black else Color.White,
                    modifier = Modifier.size(36.dp)
                        .align(Alignment.CenterStart),
                )
            }

            ColorItemContent(
                label, color,
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.Center),
            )

            Icon(
                if (favorited) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = if (color.isBrighter) Color.Black else Color.White,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .clickable { onClickFavorite() }
                    .padding(4.dp),
            )
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
fun PreviewColorListItem() {
    val items = listOf(
        "三峰結華" to HexColor("3B91C4"),
        "八宮めぐる" to HexColor("FFE012"),
        "樋口円香" to HexColor("BE1E3E"),
        "有栖川夏葉" to HexColor("90E677"),
    )

    var selected by remember { mutableStateOf(listOf("樋口円香", "有栖川夏葉")) }
    var favorited by remember { mutableStateOf(listOf("三峰結華", "八宮めぐる")) }

    fun onClick(label: String): () -> Unit = {
        selected = if (selected.contains(label))
                       selected - listOf(label)
                   else
                       selected + listOf(label)
    }

    fun onClickFavorite(label: String): () -> Unit = {
        favorited = if (favorited.contains(label))
                        favorited - listOf(label)
                    else
                        favorited + listOf(label)
    }

    Column {
        Column(Modifier.width(240.dp).background(lightBackground)) {
            items.forEach { (label, color) ->
                SelectableColorListItem(
                    label,
                    color,
                    selected = selected.contains(label),
                    favorited = favorited.contains(label),
                    onClick = onClick(label),
                    onClickFavorite = onClickFavorite(label),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }

        Column(Modifier.width(240.dp).background(darkBackground)) {
            items.forEach { (label, color) ->
                SelectableColorListItem(
                    label,
                    color,
                    selected = selected.contains(label),
                    favorited = favorited.contains(label),
                    onClick = onClick(label),
                    onClickFavorite = onClickFavorite(label),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}
