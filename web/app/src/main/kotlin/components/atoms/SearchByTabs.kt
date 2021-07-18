package components.atoms

import materialui.components.tab.tab
import materialui.components.tabs.enums.TabsIndicatorColor
import materialui.components.tabs.enums.TabsVariant
import materialui.components.tabs.tabs
import materialui.styles.makeStyles
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import react.*
import react.dom.attrs
import utilities.invoke
import utilities.useTranslation

fun RBuilder.searchByTabs(handler: RHandler<SearchByTabsProps>) = child(TabsComponent, handler = handler)

private val TabsComponent = functionComponent<SearchByTabsProps> { props ->
    val (t, _) = useTranslation()

    tabs {
        attrs {
            variant = TabsVariant.fullWidth
            indicatorColor = TabsIndicatorColor.primary
            value = props.index
            onChange = { _, index ->
                indexOf(index)?.let { props.onChangeTab(it) }
            }
        }

        SearchByTab.values().forEach { tab ->
            tab {
                attrs.label { +t(tab.labelKey) }
            }
        }
    }
}

private fun indexOf(index: Int) = SearchByTab.values().find { it.ordinal == index }

external interface SearchByTabsProps : RProps {
    var index: Int
    var onChangeTab: (SearchByTab) -> Unit
}

private external interface SearchByTabsStyle {
    val root: String
}

private val useStyles = makeStyles<SearchByTabsStyle> {
    "root" {}
}
