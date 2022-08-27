package components.templates.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import components.atoms.backdrop.BACKDROP_FRONT_HEADER_HEIGHT
import components.organisms.box.SearchBox
import material.components.TabBar
import material.components.TabContent
import material.components.TopAppBarMainContent
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

    TopAppBarMainContent(
        topAppBarVariant,
        attrsScope = {
            style {
                display(DisplayStyle.Flex)
                flexGrow(1)
                flexDirection(FlexDirection.Column)
            }
        },
    ) {
        SearchTabs(setTab)

        Div({
            style {
                flexGrow(1)
                paddingTop(24.px)
                paddingBottom(BACKDROP_FRONT_HEADER_HEIGHT.px)
                overflowY("auto")
            }
        }) { SearchBox(tab, onChange) }
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
