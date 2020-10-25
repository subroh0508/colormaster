package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.components.molecules.SelectableColorListItem
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem

private enum class UiState {
    Preview, Select
}

@Composable
fun ColorLists(
    items: List<IdolColorListItem>,
    onSelect: (IdolColor, Boolean) -> Unit = { _, _ -> },
    onDoubleClick: (IdolColor) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var uiState by remember { mutableStateOf(UiState.Preview) }
    val selectedIds = items.filter(IdolColorListItem::selected).map(IdolColorListItem::id)

    onCommit(items.map(IdolColorListItem::id)) {
        uiState = UiState.Preview
    }

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

    LazyColumnFor(
        items,
        contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp),
        modifier = modifier,
    ) { (id, name, hexColor, selected) ->
        val idolColor = IdolColor(id, name.value, hexColor)

        SelectableColorListItem(
            name.value, hexColor, selected,
            onClick = { handleOnClick(idolColor) },
            onLongClick = { handleOnLongClick(idolColor) },
            onDoubleClick = { onDoubleClick(idolColor) },
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        )
    }
}
