package components.organisms.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import components.atoms.list.AutoGridList
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.search.model.IdolColorList
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

private const val GRID_MIN_WIDTH = 216
private const val GRID_MARGIN_HORIZONTAL = 8

@Composable
fun SearchResultList(
    loadState: LoadState,
    isSignedIn: Boolean,
    header: @Composable (List<String>, (Boolean) -> Unit) -> Unit,
    errorContent: @Composable (Throwable) -> Unit,
) {
    val list: IdolColorList = loadState.getValueOrNull() ?: IdolColorList()
    val error: Throwable? = loadState.getErrorOrNull()

    val (selections, setSelections) = remember(loadState) { mutableStateOf<List<String>>(listOf()) }

    header(selections) { all ->
        setSelections(if (all) list.map { it.id } else listOf())
    }

    when {
        error != null -> errorContent(error)
        list.isNotEmpty() -> List(isSignedIn, list, selections, setSelections)
    }
}

@Composable
private fun List(
    isActionIconsVisible: Boolean,
    list: IdolColorList,
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
        list.forEach { item ->
            IdolCard(
                item,
                isActionIconsVisible = isActionIconsVisible,
                selected = selections.contains(item.id),
                inCharge = list.inCharge(item.id),
                favorite = list.favorite(item.id),
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
