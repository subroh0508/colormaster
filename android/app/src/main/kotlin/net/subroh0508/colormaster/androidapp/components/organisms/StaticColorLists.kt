package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.components.atoms.ColorListItem
import net.subroh0508.colormaster.model.HexColor
import net.subroh0508.colormaster.model.IdolColor

@Composable
fun StaticColorLists(
    items: List<IdolColor>,
) {
    Column(Modifier.fillMaxSize()) {
        items.forEach { (_, name, hexColor) ->
            SquareColorListItem(
                label = name, color = hexColor,
                modifier = Modifier.fillMaxWidth()
                    .weight(1.0F, true),
            )
        }
    }
}



@Composable
private fun SquareColorListItem(
    label: String,
    color: HexColor,
    modifier: Modifier = Modifier,
) = ColorListItem(
    label, color,
    shape = RoundedCornerShape(0.dp),
    modifier = modifier,
)
