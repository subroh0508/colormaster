package components.organisms.list

import MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import components.molecules.icon.ActionIcons
import material.components.Checkbox
import material.components.Icon
import net.subroh0508.colormaster.model.IdolColor
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun IdolCard(
    item: IdolColor,
    selected: Boolean,
    isActionIconsVisible: Boolean,
    inCharge: Boolean,
    favorite: Boolean,
    onClick: (IdolColor, Boolean) -> Unit,
    onDoubleClick: (IdolColor) -> Unit,
    onInChargeClick: (IdolColor, Boolean) -> Unit,
    onFavoriteClick: (IdolColor, Boolean) -> Unit,
    attrsScope: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
) = Div({
    classes("mdc-typography--body2")
    style {
        justifyContent(JustifyContent.Center)
        textAlign("center")
        fontWeight("bold")
        margin(4.px)
        color(if (item.isBrighter) Color.black else Color.white)
        cursor("pointer")
    }
    onClick { onClick(item, !selected) }
    onDoubleClick { onDoubleClick(item) }
    attrsScope?.invoke(this)
}) {
    Content(
        item,
        isActionIconsVisible,
        leading = { className ->
            Checkbox(
                id = "checkbox-${item.id}",
                selected,
                ripple = false,
                attrsScope = { classes(className) },
                onChange = { onClick(item, !selected) },
            )
        },
    )

    ActionIconsBar(
        item,
        isActionIconsVisible,
        inCharge,
        favorite,
        onInChargeClick,
        onFavoriteClick,
    )
}

@Composable
private fun Content(
    item: IdolColor,
    isActionIconsVisible: Boolean,
    leading: @Composable (String) -> Unit,
) {
    val style = remember(item, isActionIconsVisible) { IdolCardContentStyle(item, isActionIconsVisible) }

    Style(style)

    Div({ classes(style.content) }) {
        leading(style.checkbox)
        Div({ classes(style.text) }) {
            Text(item.name)
            Br { }
            Text(item.color)
        }
        Div({ classes(style.leading) })
    }
}

@Composable
private fun ActionIconsBar(
    item: IdolColor,
    isActionIconsVisible: Boolean,
    inCharge: Boolean,
    favorite: Boolean,
    onInChargeClick: (IdolColor, Boolean) -> Unit,
    onFavoriteClick: (IdolColor, Boolean) -> Unit,
) {
    if (!isActionIconsVisible) {
        return
    }

    val inChargeIcon = "star${if (inCharge) "" else "_border"}"
    val inFavoriteIcon = "favorite${if (favorite) "" else "_border"}"

    Style(IdolCardActionsStyle)

    ActionIcons(
        { classes(IdolCardActionsStyle.container) },
        leading = {},
        trailing = {
            if (isActionIconsVisible) {
                Icon(inChargeIcon) { onClick { onInChargeClick(item, !inCharge) } }
                Icon(inFavoriteIcon) { onClick { onFavoriteClick(item, !favorite) } }
            }
        },
    )
}

private class IdolCardContentStyle(
    item: IdolColor,
    isActionIconsVisible: Boolean,
) : StyleSheet() {
    val content = "content-${item.id}"
    val checkbox = "checkbox-${item.id}"

    private val color = if (item.isBrighter) Color.black else Color.white
    private val backgroundColor = Color(item.color)

    init {
        className(content) style {
            display(DisplayStyle.Flex)
            padding(8.px)
            property("border-style", "solid")
            property("border-color", backgroundColor)
            if (isActionIconsVisible) {
                borderRadius(16.px, 16.px, 0.px, 0.px)
                borderWidth(1.px, 1.px, 0.px)
            }
            else {
                borderRadius(16.px)
                borderWidth(1.px)
            }

            backgroundColor(backgroundColor)
        }

        className(checkbox) style {
            height(24.px)
            width(24.px)
            property("margin", "auto 0")

            desc(className(checkbox), className("mdc-checkbox")) style {
                position(Position.Relative)
                property("left", "-8px")
            }

            desc(className(checkbox), className("mdc-checkbox__background")) style {
                variable("mdc-checkbox-unchecked-color", color)
                variable("mdc-checkbox-checked-color", color)
                variable("mdc-checkbox-ink-color", backgroundColor)
            }
        }
    }

    val text by style {
        flexGrow(1)
    }

    val leading by style {
        height(24.px)
        width(24.px)
    }
}

private object IdolCardActionsStyle : StyleSheet() {
    val container by style {
        padding(4.px, 8.px)
        borderRadius(0.px, 0.px, 16.px, 16.px)
        property("border-style", "solid")
        property("border-color", MaterialTheme.Var.divider)
        borderWidth(0.px, 1.px, 1.px)
        color(MaterialTheme.Var.onSurface)
        backgroundColor(MaterialTheme.Var.surface)
    }
}
