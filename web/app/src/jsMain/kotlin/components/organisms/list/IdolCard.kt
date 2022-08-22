package components.organisms.list

import MaterialTheme
import androidx.compose.runtime.Composable
import components.molecules.card.ListItemCard
import components.molecules.icon.ActionIcons
import net.subroh0508.colormaster.model.IdolColor
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import utilities.*
import utilities.LocalDarkTheme

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
    }
    onClick { onClick(item, !selected)  }
    onDoubleClick { onDoubleClick(item) }
    attrsScope?.invoke(this)
}) {
    Content(item, isActionIconsVisible)

    if (isActionIconsVisible) {
        ActionIconsBar(
            item,
            inCharge,
            favorite,
            onClick,
            onInChargeClick,
            onFavoriteClick,
        )
    }
}

@Composable
private fun Content(
    item: IdolColor,
    isActionIconsVisible: Boolean,
) = Div({
    style {
        padding(8.px)
        property("border-style", "solid")
        property("border-color", Color(item.color))
        if (isActionIconsVisible) {
            borderRadius(16.px, 16.px, 0.px, 0.px)
            borderWidth(1.px, 1.px, 0.px)
        }
        else {
            borderRadius(16.px)
            borderWidth(1.px)
        }
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
    inCharge: Boolean,
    favorite: Boolean,
    onClick: (IdolColor, Boolean) -> Unit,
    onInChargeClick: (IdolColor, Boolean) -> Unit,
    onFavoriteClick: (IdolColor, Boolean) -> Unit,
) {
    val dark = LocalDarkTheme()

    val inChargeIcon = "star${if (inCharge) "" else "_border"}"
    val inFavoriteIcon = "favorite${if (favorite) "" else "_border"}"

    val background = Color(item.color).let {
        if (dark) it.darken(0.3) else it.lighten(0.3)
    }

    ActionIcons(
        {
            style {
                padding(4.px, 8.px)
                borderRadius(0.px, 0.px, 16.px, 16.px)
                property("border-style", "solid")
                property("border-color", MaterialTheme.Var.divider)
                borderWidth(0.px, 1.px, 1.px)
                color(MaterialTheme.Var.onSurface)
                backgroundColor(MaterialTheme.Var.surface)
            }
        },
        inChargeIcon to { onInChargeClick(item, !inCharge) },
        inFavoriteIcon to { onFavoriteClick(item, !favorite) },
    )
}
