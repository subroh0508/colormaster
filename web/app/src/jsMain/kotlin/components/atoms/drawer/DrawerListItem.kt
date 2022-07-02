package components.atoms.drawer

import MaterialTheme
import androidx.compose.runtime.Composable
import material.components.Icon
import material.components.TypographyBody1
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
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

@Composable
fun DrawerListItem(label: String, href: String) {
    Style(DrawerListItemAnchorStyle)

    A(href, {
        classes("mdc-deprecated-list-item")
        style {
            color(MaterialTheme.Var.textLink)
        }
    }) {
        Icon("launch", applyAttrs = {
            style {
                color(MaterialTheme.Var.onSurface)
                marginRight(12.px)
            }
        })
        TypographyBody1(applyAttrs = {
            classes(DrawerListItemAnchorStyle.label)
        }) { Text(label) }
    }
}

private object DrawerListItemAnchorStyle : StyleSheet() {
    val label by style {
        fontWeight("bold")

        hover(self) style  {
            textDecoration("underline")
        }
    }
}
