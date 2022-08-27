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
    header: @Composable (List<String>, (Boolean) -> Unit) -> Unit,
    errorContent: @Composable (Throwable) -> Unit
) {
    val items: List<IdolColor> = loadState.getValueOrNull() ?: listOf()
    val error: Throwable? = loadState.getErrorOrNull()

    val (selections, setSelections) = remember(loadState) { mutableStateOf<List<String>>(listOf()) }

    header(selections) { all ->
        setSelections(if (all) items.map { it.id } else listOf())
    }

    when {
        error != null -> errorContent(error)
        items.isNotEmpty() -> List(items, selections, setSelections)
    }
}

@Composable
private fun List(
    items: List<IdolColor>,
    selections: List<String>,
    setSelections: (List<String>) -> Unit,
) = AutoGridList(
    gridMinWidth = GRID_MIN_WIDTH,
    marginHorizontal = GRID_MARGIN_HORIZONTAL,
    {
        style {
            property("border-top", "1px solid ${MaterialTheme.Var.divider}")
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

private fun buildSelections(
    selections: List<String>,
    id: String,
    selected: Boolean,
) = if (selected) selections + id else selections - id
