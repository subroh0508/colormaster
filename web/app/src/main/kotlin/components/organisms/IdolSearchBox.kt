package components.organisms

import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.textfield.textField
import react.*
import react.dom.form

fun RBuilder.idolSearchBox(handler: RHandler<IdolSearchBoxProps>) = child(functionalComponent<IdolSearchBoxProps> { props ->
    list {
        listItem {
            form {
                textField {
                    attrs {
                        label { +"アイドル名" }
                        variant = FormControlVariant.outlined
                        value = props.idolName
                    }
                }
            }
        }
    }
}, handler = handler)

external interface IdolSearchBoxProps : RProps {
    var idolName: String?
}
