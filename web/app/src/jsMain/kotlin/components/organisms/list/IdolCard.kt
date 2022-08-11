package components.organisms.list

import MaterialTheme
import androidx.compose.runtime.Composable
import components.molecules.card.ListItemCard
import components.molecules.icon.ActionIcons
import material.components.Card
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
    style {
        position(Position.Relative)
        margin(4.px)
        color(if (item.isBrighter) Color.black else Color.white)
    }
}) {
    ListItemCard(
        selected,
        {
            style {
                backgroundColor(Color(item.color))
            }
            attrsScope?.invoke(this)
        },
        onClick =  { onClick(item, !selected) },
        onDoubleClick = { onDoubleClick(item) },
    ) {
        Text(item.name)
        Br { }
        Text(item.color)
    }

    if (isActionIconsVisible) {
        val inChargeIcon = "star${if (inCharge) "" else "_border"}"
        val inFavoriteIcon = "favorite${if (favorite) "" else "_border"}"

        ActionIcons(
            {
                style { marginTop(4.px) }
            },
            inChargeIcon to { onInChargeClick(item, !inCharge) },
            inFavoriteIcon to { onFavoriteClick(item, !favorite) },
        )
    }
}
