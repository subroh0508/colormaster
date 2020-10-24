package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.components.atoms.ColorListItem
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem

private enum class UiState {
    Preview, Select
}

@Composable
fun ColorLists(
    items: List<IdolColorListItem>,
    onSelect: (IdolColor, Boolean) -> Unit = { _, _ -> },
    onClick: (IdolColorListItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var uiState by remember { mutableStateOf(UiState.Preview) }
    val selectedIds = items.filter(IdolColorListItem::selected).map(IdolColorListItem::id)

    fun handleOnLongClick(item: IdolColor) {
        onSelect(item, !selectedIds.contains(item.id))
        if (uiState == UiState.Select && (selectedIds - listOf(item.id)).isEmpty()) {
            uiState = UiState.Preview
            return
        }

        if (uiState == UiState.Preview && selectedIds.isEmpty()) {
            uiState = UiState.Select
            return
        }
    }

    fun handleOnClick(item: IdolColor) {
        if (uiState == UiState.Preview) return

        handleOnLongClick(item)
    }

    val listItemModifier = Modifier.padding(vertical = 4.dp)

    LazyColumnFor(
        items,
        contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp),
        modifier = modifier,
    ) { (id, name, hexColor, selected) ->
        val idolColor = IdolColor(id, name.value, hexColor)

        ColorListItem(
            name.value, hexColor, selected,
            onClick = { handleOnClick(idolColor) },
            onLongClick = { handleOnLongClick(idolColor) },
            modifier = listItemModifier,
        )
    }
}
