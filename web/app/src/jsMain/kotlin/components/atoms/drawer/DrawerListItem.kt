package components.atoms.drawer

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import material.components.DrawerListItem as MaterialDrawerListItem

@Composable
fun DrawerListItem(
    text: String,
    activated: Boolean = false,
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
) = DrawerListItem(applyAttrs, activated) { Text(text) }

@Composable
fun DrawerListItem(
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    activated: Boolean = false,
    content: @Composable () -> Unit,
) = MaterialDrawerListItem({
    applyAttrs?.invoke(this)
    style { paddingLeft(32.px) }
}, activated, content)
