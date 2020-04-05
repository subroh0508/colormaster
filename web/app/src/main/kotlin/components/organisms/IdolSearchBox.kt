package components.organisms

import components.molecules.titleChips
import components.molecules.typesChips
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.textfield.textField
import net.subroh0508.colormaster.model.Titles
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.model.ui.idol.Filters
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

        listItem {
            titleChips {
                attrs {
                    title = props.filters.title
                    onSelect = props.onSelectTitle
                }
            }
        }

        listItem {
            typesChips(props.filters.title) {
                attrs {
                    types = props.filters.types
                    onSelect = props.onSelectTypes
                }
            }
        }
    }
}

external interface IdolSearchBoxProps : RProps {
    var idolName: String?
    var filters: Filters
    var onChangeIdolName: (String) -> Unit
    var onSelectTitle: (Titles, Boolean) -> Unit
    var onSelectTypes: (Types, Boolean) -> Unit
}
