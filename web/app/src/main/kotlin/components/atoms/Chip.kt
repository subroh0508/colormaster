package components.atoms

import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.chip.chip
import materialui.components.chip.enums.ChipColor
import materialui.components.chip.enums.ChipVariant
import materialui.styles.makeStyles
import org.w3c.dom.events.Event
import react.*
import react.dom.attrs

fun RBuilder.chip(handler: RHandler<ChipProps>) = child(chipComponent, handler = handler)

private val chipComponent = functionComponent<ChipProps> { props ->
    val classes = useStyles()

    chip {
        attrs {
            classes(classes.root)
            label { +props.label }
            color = ChipColor.primary
            variant = if (props.isChecked) ChipVariant.default else ChipVariant.outlined
            onClickFunction = { props.onClick?.invoke(it) }
        }
    }
}

external interface ChipProps : PropsWithChildren {
    var label: String
    var isChecked: Boolean
    var onClick: ((event: Event) -> Unit)?
}

private external interface ChipStyle {
    val root: String
}

private val useStyles = makeStyles<ChipStyle> {
    "root" {
        margin(4.px)
    }
}
