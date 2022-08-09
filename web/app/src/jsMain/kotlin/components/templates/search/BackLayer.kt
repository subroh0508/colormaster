package components.templates.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import components.organisms.box.SearchBox
import material.components.TabBar
import material.components.TabContent
import material.components.TopAppBarMainContent
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.px
import utilities.LocalI18n
import utilities.invoke

@Composable
fun BackLayer(
    topAppBarVariant: String,
    onChange: (SearchParams) -> Unit,
) {
    val (tab, setTab) = remember { mutableStateOf(SearchByTab.BY_NAME) }

    TopAppBarMainContent(topAppBarVariant) {
        SearchTabs(setTab)

        SearchBox(tab, onChange)
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
    ) {
        style { marginBottom(24.px) }
    }
}
