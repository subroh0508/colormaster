package components.organisms.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import components.atoms.list.AutoGridList
import net.subroh0508.colormaster.components.core.model.LoadState
import net.subroh0508.colormaster.features.myidols.rememberFetchFavoriteIdolsUseCase
import net.subroh0508.colormaster.features.myidols.rememberFetchInChargeIdolsUseCase
import net.subroh0508.colormaster.model.IdolColor
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun InChargeIdolsList(
    isSignedIn: Boolean,
    actions: @Composable (List<String>, Boolean, (Boolean) -> Unit) -> Unit,
) {
    val loadState by rememberFetchInChargeIdolsUseCase(isSignedIn)

    MyIdolsList(loadState, actions)
}

@Composable
fun FavoriteIdolsList(
    isSignedIn: Boolean,
    actions: @Composable (List<String>, Boolean, (Boolean) -> Unit) -> Unit,
) {
    val loadState by rememberFetchFavoriteIdolsUseCase(isSignedIn)

    MyIdolsList(loadState, actions)
}

@Composable
private fun MyIdolsList(
    loadState: LoadState,
    actions: @Composable (List<String>, Boolean, (Boolean) -> Unit) -> Unit,
) {
    val items: List<IdolColor> = loadState.getValueOrNull() ?: listOf()
    val (selections, setSelections) = remember(loadState) { mutableStateOf<List<String>>(listOf()) }

    if (loadState.getErrorOrNull() == null) {
        List(
            items,
            selections,
            setSelections,
            openPenlight = {},
        )
    }

    actions(selections, items.isNotEmpty()) { all ->
        setSelections(if (all) items.map { it.id } else listOf())
    }
}

@Composable
private fun List(
    items: List<IdolColor>,
    selections: List<String>,
    setSelections: (List<String>) -> Unit,
    openPenlight: (String) -> Unit,
) = AutoGridList(
    gridMinWidth = GRID_MIN_WIDTH,
    marginHorizontal = GRID_MARGIN_HORIZONTAL,
    {
        style {
            padding(8.px, 4.px)
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
                onClick = { (id), selected ->
                    setSelections(buildSelections(selections, id, selected))
                },
                onDoubleClick = { (id) -> openPenlight(id) },
            ) { style { width(width.px) } }
        }
    }
}
