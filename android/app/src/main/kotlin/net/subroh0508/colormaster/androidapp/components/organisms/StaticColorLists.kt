package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.components.atoms.ColorItemContent
import net.subroh0508.colormaster.components.core.extensions.toColor
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IntColor

@Composable
fun StaticColorLists(
    type: ScreenType,
    items: List<IdolColor>,
) {
    Column(Modifier.fillMaxSize()) {
        items.forEach { (_, name, intColor) ->
            StaticColorListItem(type, name, intColor)
        }
    }
}

@Composable
private fun ColumnScope.StaticColorListItem(
    type: ScreenType,
    label: String,
    intColor: IntColor,
) {
    val boxModifier = Modifier.fillMaxWidth()
        .weight(1.0F, true)
        .background(color = intColor.toColor())

    if (type == ScreenType.Penlight) {
        Box(modifier = boxModifier)
        return
    }

    Box(modifier = boxModifier) {
        ColorItemContent(
            label, intColor,
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.Center),
        )
    }
}
