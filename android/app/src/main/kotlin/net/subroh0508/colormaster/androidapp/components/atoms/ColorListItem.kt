package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.themes.darkBackground
import net.subroh0508.colormaster.androidapp.themes.hexToColor
import net.subroh0508.colormaster.androidapp.themes.lightBackground
import net.subroh0508.colormaster.model.HexColor

@Composable
fun ColorListItem(
    label: String,
    color: HexColor,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = 4.dp,
        backgroundColor = color.hexToColor(),
        contentColor = if (color.isBrighter) Color.Black else Color.White,
        shape = shape,
        modifier = modifier.clickable(
            onClick = onClick,
            onLongClick = onLongClick,
            onDoubleClick = onDoubleClick,
        ),
    ) {
        Box(modifier.fillMaxWidth()) {
            if (selected) {
                Icon(
                    Icons.Outlined.CheckCircle,
                    modifier = Modifier.align(Alignment.CenterStart),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.Center),
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                )
                Text(
                    "#${color.value}",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp, start = 24.dp, end = 24.dp),
                )
            }
        }
    }
}

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

    fun onClick(label: String): () -> Unit = {
        selected = if (selected.contains(label))
                       selected - listOf(label)
                   else
                       selected + listOf(label)
    }

    Column {
        Column(Modifier.width(240.dp).background(lightBackground)) {
            items.forEach { (label, color) ->
                ColorListItem(
                    label,
                    color,
                    selected = selected.contains(label),
                    onClick = onClick(label),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }

        Column(Modifier.width(240.dp).background(darkBackground)) {
            items.forEach { (label, color) ->
                ColorListItem(
                    label,
                    color,
                    selected = selected.contains(label),
                    onClick = onClick(label),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}
