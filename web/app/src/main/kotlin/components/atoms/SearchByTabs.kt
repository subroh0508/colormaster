package components.atoms

import materialui.components.tab.tab
import materialui.components.tabs.enums.TabsVariant
import materialui.components.tabs.tabs
import materialui.styles.makeStyles
import react.*
import utilities.invoke
import utilities.useTranslation

fun RBuilder.searchByTabs(handler: RHandler<TabsProps>) = child(TabsComponent, handler = handler)

private val TabsComponent = functionalComponent<TabsProps> { props ->
    val (t, _) = useTranslation()

    tabs {
        attrs {
            variant = TabsVariant.fullWidth
            value = props.index
            onChange = { _, index ->
                indexOf(index)?.let { props.onChangeTab(it) }
            }
        }

        SearchByTabs.values().forEach { tab ->
            tab {
                attrs.label {
                    +when (tab) {
                        SearchByTabs.BY_NAME -> t("searchBox.tabs.name")
                        SearchByTabs.BY_LIVE -> t("searchBox.tabs.live")
                    }
                }
            }
        }
    }
}

enum class SearchByTabs {
    BY_NAME, BY_LIVE
}

private fun indexOf(index: Int) = SearchByTabs.values().find { it.ordinal == index }

external interface TabsProps : RProps {
    var index: Int
    var onChangeTab: (SearchByTabs) -> Unit
}

private external interface TabsStyle {
    val root: String
}

private val useStyle = makeStyles<TabsStyle> {
    "root" {}
}
