package components.atoms.topappbar

import androidx.compose.runtime.Composable
import material.components.IconButton
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.HTMLButtonElement

@Composable
fun TopAppActionIcon(
    icon: String,
    applyAttrs: (AttrsScope<HTMLButtonElement>.() -> Unit)? = null,
    tooltip: @Composable (String) -> Unit,
) {
    IconButton(icon, applyAttrs = {
        classes("mdc-top-app-bar__action-item")
        applyAttrs?.invoke(this)
        attr("aria-describedby", icon)
    })

    tooltip(icon)
}

