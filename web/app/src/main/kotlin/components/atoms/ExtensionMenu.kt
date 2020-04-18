package components.atoms

import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.popover.enums.PopoverOriginHorizontal
import materialui.components.popover.enums.PopoverOriginVertical
import materialui.components.popover.horizontal
import materialui.components.popover.vertical
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*

fun RBuilder.extensionMenu(handler: RHandler<ExtensionMenuProps>) = child(ExtensionMenuComponent, handler = handler)

private val ExtensionMenuComponent = functionalComponent<ExtensionMenuProps> { props ->
    val (anchorEl, setAnchorEl) = useState<EventTarget?>(null)

    fun handleMenuOpen(event: Event) = setAnchorEl(event.currentTarget)
    fun handleMenuClose(event: Event) = setAnchorEl(null)

    iconButton {
        attrs {
            onClickFunction = ::handleMenuOpen
        }

        icon {
            +"arrow_drop_down_icon"
        }
    }

    menu {
        attrs {
            setProp("anchorEl", anchorEl)
            keepMounted = true
            getContentAnchorEl = null
            open = anchorEl != null
            onClose = { e, _ -> handleMenuClose(e) }
            anchorOrigin {
                vertical(30)
                horizontal(-10)
            }
            transformOrigin {
                vertical(PopoverOriginVertical.top)
                horizontal(PopoverOriginHorizontal.center)
            }
        }

        props.menuItems.forEach { itemProps ->
            menuItem {
                key = itemProps.id
                attrs.onClickFunction = {
                    itemProps.onClick(it, itemProps.id)
                    handleMenuClose(it)
                }
                +itemProps.label
            }
        }
    }
}

external interface ExtensionMenuProps : RProps {
    var menuItems: List<ExtensionMenuItemProps>
}

fun ExtensionMenuProps.menuItems(vararg item: ExtensionMenuItemProps.() -> Unit) {
    menuItems = item.map { jsObject(it) }
}

external interface ExtensionMenuItemProps : RProps {
    var id: String
    var label: String
    var onClick: (Event, String) -> Unit
}
