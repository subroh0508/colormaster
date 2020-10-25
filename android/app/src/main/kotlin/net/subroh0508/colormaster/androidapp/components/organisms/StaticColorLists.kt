package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.subroh0508.colormaster.androidapp.components.atoms.SquareColorListItem
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
