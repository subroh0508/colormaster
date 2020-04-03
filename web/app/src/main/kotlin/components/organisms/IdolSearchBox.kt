package components.organisms

import kotlinx.html.js.onChangeFunction
import kotlinx.html.onChange
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.textfield.textField
import react.*
import react.dom.form
import utilities.inputTarget

fun RBuilder.idolSearchBox(handler: RHandler<IdolSearchBoxProps>) = child(IdolSearchBoxComponent, handler = handler)

private val IdolSearchBoxComponent = functionalComponent<IdolSearchBoxProps> { props ->
    list {
        listItem {
            form {
                textField {
                    attrs {
                        label { +"アイドル名" }
                        variant = FormControlVariant.outlined
                        value = props.idolName
                        onChangeFunction = { e -> props.onChangeIdolName(e.inputTarget().value) }
                    }
                }
            }
        }
    }
}

external interface IdolSearchBoxProps : RProps {
    var idolName: String?
    var onChangeIdolName: (String) -> Unit
}
