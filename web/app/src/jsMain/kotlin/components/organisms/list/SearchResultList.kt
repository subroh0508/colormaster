package components.organisms.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import components.atoms.list.AutoGridList
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.common.LoadState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

private const val GRID_MIN_WIDTH = 216
private const val GRID_MARGIN_HORIZONTAL = 8

@Composable
fun SearchResultList(
    loadState: LoadState,
) {
    val items: List<IdolColor> = loadState.getValueOrNull() ?: return

    val (selections, setSelections) = remember(loadState) { mutableStateOf<List<String>>(listOf()) }

    AutoGridList(
        gridMinWidth = GRID_MIN_WIDTH,
        marginHorizontal = GRID_MARGIN_HORIZONTAL,
        {
            style {
                padding(8.px, 4.px, 56.px)
                overflowY("auto")
            }
        },
    ) { width ->
        Div({
            style {
                display(DisplayStyle.Flex)
                flexGrow(1)
                flexFlow(FlexDirection.Row, FlexWrap.Wrap)
            }
        }) {
            items.forEach { item ->
                IdolCard(
                    item,
                    isActionIconsVisible = false,
                    selected = selections.contains(item.id),
                    inCharge = false,
                    favorite = false,
                    onClick = { (id), selected ->
                        setSelections(buildSelections(selections, id, selected))
                    },
                    onDoubleClick = { _ -> },
                    onInChargeClick = { _, _ -> },
                    onFavoriteClick = { _, _ -> },
                ) { style { width(width.px) } }
            }
        }
    }
}

private fun buildSelections(
    selections: List<String>,
    id: String,
    selected: Boolean,
) = if (selected) selections + id else selections - id
