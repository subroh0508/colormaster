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
import net.subroh0508.colormaster.presentation.common.external.invoke
import net.subroh0508.colormaster.presentation.common.ui.LocalI18n
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import routes.CurrentLocalRouter

@Composable
fun BackLayer(
    topAppBarVariant: String,
    tab: SearchByTab,
    onChange: (SearchParams) -> Unit,
) {
    val router = CurrentLocalRouter() ?: return

    Style(BackLayerStyle)

    TopAppBarMainContent(
        topAppBarVariant,
        attrsScope = { classes(BackLayerStyle.main) },
    ) {
        SearchTabs(tab) {
            router.toSearch(
                when (it) {
                    SearchByTab.BY_NAME -> SearchParams.ByName.EMPTY
                    SearchByTab.BY_LIVE -> SearchParams.ByLive.EMPTY
                }
            )
        }

        Div({ classes(BackLayerStyle.content) }) {
            SearchBox(tab, onChange)
        }
    }
}

@Composable
private fun SearchTabs(activeTab: SearchByTab, onChange: (SearchByTab) -> Unit) {
    val t = LocalI18n() ?: return

    TabBar(
        SearchByTab.values().indexOf(activeTab),
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
