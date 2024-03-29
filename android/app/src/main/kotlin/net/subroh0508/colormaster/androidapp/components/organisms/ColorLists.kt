package net.subroh0508.colormaster.androidapp.components.organisms

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.atoms.OutlinedButton
import net.subroh0508.colormaster.androidapp.components.molecules.SelectableColorListItem
import net.subroh0508.colormaster.model.IdolColor

private enum class UiState {
    Preview, Select
}

@ExperimentalFoundationApi
@Composable
fun ColorLists(
    items: List<IdolColor>,
    selections: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (IdolColor, Boolean) -> Unit = { _, _ -> },
    onClick: (IdolColor) -> Unit = {},
    onClickFavorite: (IdolColor, Boolean) -> Unit = { _, _ -> },
    onPreviewClick: () -> Unit = {},
    onPenlightClick: () -> Unit = {},
    onAllClick: (Boolean) -> Unit = {},
) {
    var uiState by remember { mutableStateOf(UiState.Preview) }

    LaunchedEffect(items.map(IdolColor::id)) {
        uiState = UiState.Preview
    }

    fun handleOnLongClick(item: IdolColor) {
        onSelect(item, !selections.contains(item.id))
        if (uiState == UiState.Select && (selections - listOf(item.id)).isEmpty()) {
            uiState = UiState.Preview
            return
        }

        if (uiState == UiState.Preview && selections.isEmpty()) {
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
        LazyColumn(
            modifier = Modifier.padding(
                start = 4.dp, top = 8.dp, end = 4.dp, bottom = 52.dp,
            ),
            contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp)
        ) {
            items(items.size, { items[it].id }) { index ->
                val (id, name, intColor/*, selected, favorited*/) = items[index]
                val idolColor = IdolColor(id, name, intColor)

                SelectableColorListItem(
                    name, intColor,
                    selected = /* selected */ false,
                    favorited = /* favorited */ false,
                    onClick = { handleOnClick(idolColor) },
                    onClickFavorite = { onClickFavorite(idolColor, /* !favorited */ false) },
                    onLongClick = { handleOnLongClick(idolColor) },
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp),
                )
            }
        }

        BottomButtons(
            isEmpty = selections.isEmpty(),
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
    modifier: Modifier = Modifier,
    onPenlightClick: () -> Unit = {},
    onPreviewClick: () -> Unit = {},
    onAllClick: (Boolean) -> Unit = {},
) = Column(modifier) {
    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .12F))
    Row(Modifier.padding(8.dp)) {
        OutlinedButton(
            stringResource(R.string.search_box_bottom_preview),
            painter = painterResource(R.drawable.ic_palette_24dp),
            onClick = onPreviewClick,
            enabled = !isEmpty,
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 0.dp,
                bottomStart = 4.dp,
                bottomEnd = 0.dp,
            ),
            modifier = Modifier.weight(1.0F, true),
        )

        OutlinedButton(
            stringResource(R.string.search_box_bottom_penlight),
            painter = painterResource(R.drawable.ic_highlight_24dp),
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
            painter = painterResource(toggleAssetRes),
            onClick = { onAllClick(isEmpty) },
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 4.dp,
                bottomStart = 0.dp,
                bottomEnd = 4.dp,
            ),
            modifier = Modifier.weight(1.0F, true),
        )
    }
}
