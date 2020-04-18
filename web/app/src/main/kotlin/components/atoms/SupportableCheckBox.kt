package components.atoms

import kotlinx.html.js.onClickFunction
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import org.w3c.dom.events.Event
import react.*

fun RBuilder.supportableCheckBox(handler: RHandler<SupportableCheckBoxProps>) = child(SupportableCheckBoxComponent, handler = handler)

private val SupportableCheckBoxComponent = functionalComponent<SupportableCheckBoxProps> { props ->
    fun handleOnClick(event: Event) {
        if (props.clearedAll) props.onClickClearedAll(event) else props.onClickCheckedAll(event)
    }

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
}

external interface SupportableCheckBoxProps : RProps {
    var clearedAll: Boolean
    var onClickCheckedAll: (Event) -> Unit
    var onClickClearedAll: (Event) -> Unit
}
