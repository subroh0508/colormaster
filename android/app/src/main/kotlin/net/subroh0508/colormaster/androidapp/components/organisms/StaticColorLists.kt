package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.components.atoms.ColorItemContent
import net.subroh0508.colormaster.androidapp.themes.hexToColor
import net.subroh0508.colormaster.model.HexColor
import net.subroh0508.colormaster.model.IdolColor

@Composable
fun StaticColorLists(
    type: ScreenType,
    items: List<IdolColor>,
) {
    Column(Modifier.fillMaxSize()) {
        items.forEach { (_, name, hexColor) ->
            StaticColorListItem(type, name, hexColor)
        }
    }
}

@Composable
private fun ColumnScope.StaticColorListItem(
    type: ScreenType,
    label: String,
    color: HexColor,
) {
    val boxModifier = Modifier.fillMaxWidth()
        .weight(1.0F, true)
        .background(color = color.hexToColor())

    if (type == ScreenType.Penlight) {
        Box(modifier = boxModifier)
        return
    }

    Box(modifier = boxModifier) {
        ColorItemContent(
            label, color,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.Center),
        )
    }
}
