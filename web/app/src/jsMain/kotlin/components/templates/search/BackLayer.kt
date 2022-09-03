package components.templates.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import components.atoms.backdrop.BACKDROP_FRONT_HEADER_HEIGHT
import components.atoms.backdrop.WIDE_BACK_LAYER_WIDTH
import components.organisms.box.SearchBox
import material.components.TabBar
import material.components.TabContent
import material.components.TopAppBarMainContent
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import utilities.LocalI18n
import utilities.invoke

@Composable
fun BackLayer(
    topAppBarVariant: String,
    onChange: (SearchParams) -> Unit,
) {
    val (tab, setTab) = remember { mutableStateOf(SearchByTab.BY_NAME) }

    Style(BackLayerStyle)

    TopAppBarMainContent(
        topAppBarVariant,
        attrsScope = { classes(BackLayerStyle.main) },
    ) {
        SearchTabs(setTab)

        Div({ classes(BackLayerStyle.content) }) {
            SearchBox(tab, onChange)
        }
    }
}

@Composable
private fun SearchTabs(onChange: (SearchByTab) -> Unit) {
    val t = LocalI18n() ?: return

    TabBar(
        0,
        SearchByTab.values().map { tab ->
            TabContent(t(tab.labelKey)) { onChange(tab) }
        },
    )
}

private object BackLayerStyle : StyleSheet() {
    val main by style {
        display(DisplayStyle.Flex)
        width(100.percent)
        flexDirection(FlexDirection.Column)

        media(MEDIA_QUERY_TABLET_SMALL) {
            self style {
                width(WIDE_BACK_LAYER_WIDTH.px)
            }
        }
    }

    val content by style {
        flexGrow(1)
        paddingTop(24.px)
        paddingBottom(BACKDROP_FRONT_HEADER_HEIGHT.px)
        overflowY("auto")
    }
}
