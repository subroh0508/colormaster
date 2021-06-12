package components.molecules

import components.atoms.extensionMenu
import components.atoms.menuItems
import kotlinx.html.js.onClickFunction
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.attrs

fun RBuilder.supportableCheckBox(handler: RHandler<SupportableCheckBoxProps>) = child(
    SupportableCheckBoxComponent, handler = handler)

private val SupportableCheckBoxComponent = functionalComponent<SupportableCheckBoxProps> { props ->
    fun handleOnClick(event: Event) = if (props.clearedAll) props.onClickClearedAll(event) else props.onClickCheckedAll(event)

    iconButton {
        attrs {
            onClickFunction = ::handleOnClick
        }

        icon {
            + if (props.clearedAll)
                  "indeterminate_check_box_icon"
              else
                  "check_box_outline_blank_icon"
        }
    }

    extensionMenu {
        attrs.menuItems(
            { id = CHECKED_ALL_ID; label = "すべて"; onClick = { e, _ -> props.onClickCheckedAll(e) } },
            { id = CLEARED_ALL_ID; label = "選択解除"; onClick = { e, _ -> props.onClickClearedAll(e) } }
        )
    }
}

private const val CHECKED_ALL_ID = "checked-all"
private const val CLEARED_ALL_ID = "cleared-all"

external interface SupportableCheckBoxProps : RProps {
    var clearedAll: Boolean
    var onClickCheckedAll: (Event) -> Unit
    var onClickClearedAll: (Event) -> Unit
}
