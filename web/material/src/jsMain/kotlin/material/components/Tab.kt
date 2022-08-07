package material.components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import material.externals.MDCTab
import material.externals.MDCTabBar
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

data class TabContent(
    val label: String,
    val icon: String? = null,
    val onClick: (SyntheticMouseEvent) -> Unit = {},
)

@Composable
fun TabBar(active: Int, vararg tabContent: TabContent) = TabBar(active, tabContent.toList())

@Composable
fun TabBar(
    active: Int,
    tabContents: List<TabContent>,
) {
    var element by remember { mutableStateOf<HTMLElement?>(null) }
    var index by remember { mutableStateOf(active) }

    SideEffect {
        element?.let { MDCTabBar(it) }
    }

    Div({
        classes("mdc-tab-bar")
        attr("role", "tab-list")
        ref {
            element = it
            onDispose { element = null }
        }
    }) {
        Div({ classes("mdc-tab-scroller") }) {
            Div({ classes("mdc-tab-scroller__scroll-area") }) {
                Div({ classes("mdc-tab-scroller__scroll-content") }) {
                    tabContents.forEachIndexed { i, (label, icon, onClick) ->
                        Tab(label, icon, i == index) {
                            index = i
                            onClick(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Tab(
    label: String,
    icon: String? = null,
    active: Boolean = false,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) {
    var element by remember { mutableStateOf<HTMLElement?>(null) }

    SideEffect {
        element?.let { MDCTab(it) }
    }

    Button({
        classes(*tabClasses(active))
        attr("role", "tab")
        if (active) {
            attr("aria-selected", "true")
        }
        ref {
            element = it
            onDispose { element = null }
        }
        onClick(onClick)
    }) {
        Span({ classes("mdc-tab__content") }) {
            TabContentIcon(icon)
            Span({ classes(*tabTextLabelClasses(active)) }) {
                Text(label)
            }
        }
        TabIndicator(active)
        Span({ classes("mdc-tab__ripple") })
    }
}

@Composable
private fun TabIndicator(active: Boolean) {
    Span({ classes(*tabIndicatorClasses(active)) }) {
        Span({ classes("mdc-tab-indicator__content", "mdc-tab-indicator__content--underline") })
    }

}


private fun tabClasses(active: Boolean) = listOfNotNull(
    "mdc-tab",
    if (active) "mdc-tab--active" else null,
).toTypedArray()

private fun tabTextLabelClasses(active: Boolean) = listOfNotNull(
    "mdc-tab__text-label",
    if (!active) "mdc-theme-text-secondary" else null,
).toTypedArray()

private fun tabIndicatorClasses(active: Boolean) = listOfNotNull(
    "mdc-tab-indicator",
    if (active) "mdc-tab-indicator--active" else null,
).toTypedArray()

@Composable
private fun TabContentIcon(icon: String?) {
    icon ?: return

    I({
        classes("material-icons", "mdc-tab__icon")
        attr("aria-hidden", "true")
    }) { Text(icon) }
}