package material.components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import material.externals.MDCTopAppBar
import org.jetbrains.compose.web.attributes.AttrsScope
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
    mainContent: (@Composable () -> Unit)? = null,
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

    mainContent?.let { TopAppBarMainContent(variant, content = it) }
}

@Composable
fun TopAppNavigationIcon(
    icon: String,
    ariaLabel: String? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = IconButton(icon) {
    classes("material-icons", "mdc-top-app-bar__navigation-icon")
    ariaLabel?.let { attr("aria-label", it) }
    onClick(onClick)
}

@Composable
fun TopAppTitle(
    text: String,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = Span({
    classes("mdc-top-app-bar__title")

    onClick(onClick)
}) { Text(text) }

@Composable
fun TopAppBarMainContent(
    variant: String? = null,
    attrsScope: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Main({
    classes(topAppBarMainClass(variant))
    attrsScope?.invoke(this)
}) { content() }

@Composable
fun TopAppActionIcon(
    icon: String,
    ariaLabel: String? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = IconButton(icon) {
    classes("mdc-top-app-bar__action-item", "mdc-icon-button")
    ariaLabel?.let { attr("aria-label", it) }
    onClick(onClick)
}

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
