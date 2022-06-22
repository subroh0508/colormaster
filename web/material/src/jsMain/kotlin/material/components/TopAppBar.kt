package material.components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import material.externals.MDCTopAppBar
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

object TopAppBarVariant {
    const val Short = "short"
    const val ShortCollapse = "short-collapsed"
    const val Fixed = "fixed"
    const val Prominent = "prominent"
    const val Dense = "dense"
}

@Composable
fun TopAppBar(
    variant: String? = null,
    navigationContent: (@Composable () -> Unit)? = null,
    actionContent: (@Composable () -> Unit)? = null,
    mainContent: @Composable () -> Unit,
) {
    var element by remember { mutableStateOf<HTMLElement?>(null) }

    SideEffect {
        element?.let { MDCTopAppBar(it) }
    }

    Header({
        classes(*topAppBarClasses(variant))
        ref {
            element = it
            onDispose { element = null }
        }
    }) {
        Div({ classes("mdc-top-app-bar__row") }) {
            navigationContent?.let { AlignStartSection(it) }
            actionContent?.let { AlignEndSection(it) }
        }
    }

    Main({ classes(topAppBarMainClass(variant)) }) {
        mainContent()
    }
}

@Composable
fun TopAppNavigationIcon(
    icon: String,
    ariaLabel: String? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = Icon(icon, {
    classes("material-icons", "mdc-top-app-bar__navigation-icon")
    ariaLabel?.let { attr("aria-label", it) }
}, onClick)

@Composable
fun TopAppTitle(
    text: String,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = Span({
    classes("mdc-top-app-bar__title")

    onClick(onClick)
}) { Text(text) }

@Composable
fun TopAppActionIcon(
    icon: String,
    ariaLabel: String? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = Icon(icon, {
    classes("mdc-top-app-bar__action-item", "mdc-icon-button")
    ariaLabel?.let { attr("aria-label", it) }
}, onClick)

@Composable
private fun AlignStartSection(content: @Composable () -> Unit) {
    Section({
        classes("mdc-top-app-bar__section", "mdc-top-app-bar__section--align-start")
    }) { content() }
}

@Composable
private fun AlignEndSection(content: @Composable () -> Unit) {
    Section({
        classes("mdc-top-app-bar__section", "mdc-top-app-bar__section--align-end")
        attr("role", "toolbar")
    }) { content() }
}

private fun topAppBarClasses(variant: String?) = listOfNotNull(
    "mdc-top-app-bar",
    if (variant == TopAppBarVariant.ShortCollapse) "mdc-top-app-bar--short" else null,
    variant?.let { "mdc-top-app-bar--$it" },
).toTypedArray()

private fun topAppBarMainClass(variant: String?) = "mdc-top-app-bar--" + when (variant) {
    TopAppBarVariant.Short, TopAppBarVariant.Prominent, TopAppBarVariant.Dense -> "$variant-"
    TopAppBarVariant.ShortCollapse -> "${TopAppBarVariant.Short}-"
    else -> ""
} + "fixed-adjust"
