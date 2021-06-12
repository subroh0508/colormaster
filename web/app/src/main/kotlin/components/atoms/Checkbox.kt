package components.atoms

import kotlinx.css.color
import kotlinx.html.js.onChangeFunction
import materialui.components.checkbox.checkbox
import materialui.components.checkbox.enums.CheckboxColor
import materialui.components.formcontrollabel.formControlLabel
import materialui.styles.makeStyles
import materialui.styles.palette.primary
import org.w3c.dom.events.Event
import react.*
import react.dom.attrs
import react.dom.span

fun RBuilder.checkbox(handler: RHandler<CheckboxProps>) = child(CheckboxComponent, handler = handler)

private val CheckboxComponent = functionalComponent<CheckboxProps> { props ->
    val classes = useStyles()

    formControlLabel {
        attrs.control {
            checkbox {
                attrs {
                    color = CheckboxColor.primary
                    checked = props.isChecked
                    onChangeFunction = props.onClick ?: {}
                }
            }
        }
        attrs.label { span(classes.label) { +props.label } }
    }
}

external interface CheckboxProps : RProps {
    var label: String
    var isChecked: Boolean
    var onClick: ((event: Event) -> Unit)?
}

private external interface CheckboxStyle {
    val label: String
}

private val useStyles = makeStyles<CheckboxStyle> {
    "label" {
        color = theme.palette.text.primary
    }
}
