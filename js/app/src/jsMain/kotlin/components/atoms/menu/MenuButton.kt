package components.atoms.menu

import androidx.compose.runtime.Composable
import components.atoms.button.TextButton
import material.components.Icon
import material.components.Menu
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLButtonElement

@Composable
fun MenuButton(
    id: String,
    applyAttrs: (AttrsScope<HTMLButtonElement>.() -> Unit)? = null,
    icon: String,
    tooltip: @Composable (String) -> Unit,
    menuContent: @Composable () -> Unit,
) {
    Menu(
        anchor = { menu ->
            TextButton(
                {
                    applyAttrs?.invoke(this)
                    attr("aria-describedby", id)
                    onClick { menu?.open = true }
                },
                label = { Icon(icon) },
                trailingIcon = { Text("expand_more") },
            )
        },
    ) { menuContent() }

    tooltip(id)
}
