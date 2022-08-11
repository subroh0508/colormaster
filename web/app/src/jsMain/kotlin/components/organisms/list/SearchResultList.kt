package components.organisms.list

import androidx.compose.runtime.Composable
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

    AutoGridList(
        gridMinWidth = GRID_MIN_WIDTH,
        marginHorizontal = GRID_MARGIN_HORIZONTAL,
        {
            style {
                marginTop(64.px)
                padding(8.px, 4.px, 8.px)
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
                    selected = false,
                    inCharge = false,
                    favorite = false,
                    onClick = { _, _ -> },
                    onDoubleClick = { _ -> },
                    onInChargeClick = { _, _ -> },
                    onFavoriteClick = { _, _ -> },
                ) { style { width(width.px) } }
            }
        }
    }
}
