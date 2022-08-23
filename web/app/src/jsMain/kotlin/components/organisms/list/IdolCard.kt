package components.organisms.list

import MaterialTheme
import androidx.compose.runtime.Composable
import components.molecules.icon.ActionIcons
import material.components.Checkbox
import material.components.Icon
import net.subroh0508.colormaster.model.IdolColor
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
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
    Content(item)

    ActionIconsBar(
        item,
        selected,
        isActionIconsVisible,
        inCharge,
        favorite,
        onClick,
        onInChargeClick,
        onFavoriteClick,
    )
}

@Composable
private fun Content(item: IdolColor) = Div({
    style {
        padding(8.px)
        property("border-style", "solid")
        property("border-color", Color(item.color))
        borderRadius(16.px, 16.px, 0.px, 0.px)
        borderWidth(1.px, 1.px, 0.px)
        backgroundColor(Color(item.color))
    }
}) {
    Text(item.name)
    Br { }
    Text(item.color)
}

@Composable
private fun ActionIconsBar(
    item: IdolColor,
    selected: Boolean,
    isActionIconsVisible: Boolean,
    inCharge: Boolean,
    favorite: Boolean,
    onClick: (IdolColor, Boolean) -> Unit,
    onInChargeClick: (IdolColor, Boolean) -> Unit,
    onFavoriteClick: (IdolColor, Boolean) -> Unit,
) {
    val inChargeIcon = "star${if (inCharge) "" else "_border"}"
    val inFavoriteIcon = "favorite${if (favorite) "" else "_border"}"

    Style(IdolCardActionsStyle)

    ActionIcons(
        { classes(IdolCardActionsStyle.container) },
        leading = {
            Checkbox(
                id = "checkbox-${item.id}",
                selected,
                ripple = false,
                attrsScope = { classes(IdolCardActionsStyle.checkbox) },
                onChange = { onClick(item, !selected) },
            )
        },
        trailing = {
            if (isActionIconsVisible) {
                Icon(inChargeIcon) { onClick { onInChargeClick(item, !inCharge) } }
                Icon(inFavoriteIcon) { onClick { onFavoriteClick(item, !favorite) } }
            }
        },
    )
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

    val checkbox by style {
        height(24.px)
        width(24.px)

        desc(self, className("mdc-checkbox__background")) style {
            variable("mdc-checkbox-unchecked-color", MaterialTheme.Var.onSurface)
            variable("mdc-checkbox-checked-color", MaterialTheme.Var.onSurface)
        }
    }
}
