package components.atoms.topappbar

import androidx.compose.runtime.Composable
import material.components.Icon
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLElement

@Composable
fun TopAppActionIcon(
    icon: String,
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    tooltip: @Composable (String) -> Unit,
) {
    Icon(icon, applyAttrs = {
        classes("mdc-top-app-bar__action-item", "mdc-icon-button")
        applyAttrs?.invoke(this)
        attr("aria-describedby", icon)
    })

    tooltip(icon)
}

