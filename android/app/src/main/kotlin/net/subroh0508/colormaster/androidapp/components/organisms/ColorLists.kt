package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.components.atoms.ColorListItem
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem

@Composable
fun ColorLists(
    items: List<IdolColorListItem>,
    onClick: (IdolColorListItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val listItemModifier = Modifier.padding(vertical = 4.dp)

    LazyColumnFor(
        items,
        contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp),
        modifier = modifier,
    ) { item ->
        ColorListItem(
            item.name.value,
            item.hexColor,
            onClick = { onClick(item) },
            modifier = listItemModifier,
        )
    }
}
