package components.organisms

import components.molecules.titleChips
import components.molecules.typesChips
import kotlinx.css.pct
import kotlinx.css.width
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.textfield.textField
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import react.*
import react.dom.form
import utilities.inputTarget
import utilities.invoke
import utilities.useTranslation

fun RBuilder.idolSearchBox(handler: RHandler<IdolSearchBoxProps>) = child(IdolSearchBoxComponent, handler = handler)

private val IdolSearchBoxComponent = functionalComponent<IdolSearchBoxProps> { props ->
    val classes = useStyle()
    val (t, _) = useTranslation()

    list {
        listItem {
            form(classes = classes.form) {
                textField {
                    attrs {
                        classes(classes.textField)
                        label { +t("searchBox.attributes.idolName") }
                        variant = FormControlVariant.outlined
                        value = props.params.idolName?.value
                        onChangeFunction = { e -> props.onChangeIdolName(e.inputTarget().value) }
                    }
                }
            }
        }

        listItem {
            titleChips {
                attrs {
                    title = props.params.brands
                    onSelect = props.onSelectTitle
                }
            }
        }

        listItem {
            typesChips(props.params.brands) {
                attrs {
                    types = props.params.types
                    onSelect = props.onSelectType
                }
            }
        }
    }
}

external interface IdolSearchBoxProps : RProps {
    var params: SearchParams
    var onChangeIdolName: (String) -> Unit
    var onSelectTitle: (Brands, Boolean) -> Unit
    var onSelectType: (Types, Boolean) -> Unit
}

private external interface IdolSearchBoxStyle {
    val form: String
    val textField: String
}

private val useStyle = makeStyles<IdolSearchBoxStyle> {
    "form" {
        width = 100.pct
    }
    "textField" {
        width = 100.pct
    }
}
