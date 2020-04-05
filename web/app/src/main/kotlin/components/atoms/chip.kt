package components.atoms

import materialui.components.chip.chip
import materialui.components.chip.enums.ChipColor
import materialui.components.chip.enums.ChipVariant
import org.w3c.dom.events.Event
import react.*

fun RBuilder.chip(handler: RHandler<ChipProps>) = child(chipComponent, handler = handler)

private val chipComponent = functionalComponent<ChipProps> { props ->
    chip {
        attrs {
            label { +props.label }
            color = ChipColor.primary
            variant = if (props.isChecked) ChipVariant.outlined else ChipVariant.default
        }
    }
}

external interface ChipProps : RProps {
    var label: String
    var isChecked: Boolean
    var onClick: ((event: Event) -> Unit)?
}
