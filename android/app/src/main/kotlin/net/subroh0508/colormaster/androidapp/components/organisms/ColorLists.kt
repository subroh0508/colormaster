package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.atoms.OutlinedButton
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
    onClick: (IdolColor) -> Unit = {},
    onPreviewClick: () -> Unit = {},
    onPenlightClick: () -> Unit = {},
    onAllClick: (Boolean) -> Unit = {},
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
        if (uiState == UiState.Preview) {
            onClick(item)
            return
        }

        handleOnLongClick(item)
    }

    Box(modifier) {
        LazyColumnFor(
            items,
            contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp),
            modifier = Modifier.padding(
                start = 4.dp, top = 8.dp, end = 4.dp, bottom = 52.dp,
            ),
        ) { (id, name, hexColor, selected) ->
            val idolColor = IdolColor(id, name.value, hexColor)

            SelectableColorListItem(
                name.value, hexColor, selected,
                onClick = { handleOnClick(idolColor) },
                onLongClick = { handleOnLongClick(idolColor) },
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
            )
        }

        BottomButtons(
            isEmpty = selectedIds.isEmpty(),
            onPreviewClick = onPreviewClick,
            onPenlightClick = onPenlightClick,
            onAllClick = onAllClick,
            modifier = Modifier.align(Alignment.BottomCenter)
                .background(color = MaterialTheme.colors.surface),
        )
    }
}

@Composable
private fun BottomButtons(
    isEmpty: Boolean,
    onPenlightClick: () -> Unit = {},
    onPreviewClick: () -> Unit = {},
    onAllClick: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) = Column(modifier) {
    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .12F))
    Row(Modifier.padding(8.dp)) {
        OutlinedButton(
            stringResource(R.string.search_box_bottom_preview),
            vectorResource(R.drawable.ic_palette_24dp),
            onClick = onPreviewClick,
            enabled = !isEmpty,
            shape = RoundedCornerShape(
                topLeft = 4.dp,
                topRight = 0.dp,
                bottomLeft = 4.dp,
                bottomRight = 0.dp,
            ),
            modifier = Modifier.weight(1.0F, true),
        )

        OutlinedButton(
            stringResource(R.string.search_box_bottom_penlight),
            vectorResource(R.drawable.ic_highlight_24dp),
            onClick = onPenlightClick,
            enabled = !isEmpty,
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.weight(1.0F, true),
        )

        val (toggleLabelRes, toggleAssetRes) =
            if (isEmpty)
                R.string.search_box_bottom_all to R.drawable.ic_check_box_24dp
            else
                R.string.search_box_bottom_clear to R.drawable.ic_indeterminate_check_box_24dp

        OutlinedButton(
            stringResource(toggleLabelRes),
            vectorResource(toggleAssetRes),
            onClick = { onAllClick(isEmpty) },
            shape = RoundedCornerShape(
                topLeft = 0.dp,
                topRight = 4.dp,
                bottomLeft = 0.dp,
                bottomRight = 4.dp,
            ),
            modifier = Modifier.weight(1.0F, true),
        )
    }
}
