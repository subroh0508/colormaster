package components.templates.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import components.organisms.box.SearchBox
import material.components.TabBar
import material.components.TabContent
import material.components.TopAppBarMainContent
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import utilities.LocalI18n
import utilities.invoke

@Composable
fun BackLayer(
    topAppBarVariant: String,
) {
    val tab = remember { mutableStateOf(SearchByTab.BY_NAME) }

    TopAppBarMainContent(topAppBarVariant) {
        SearchTabs { tab.value = it }

        SearchBox(tab.value)
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
