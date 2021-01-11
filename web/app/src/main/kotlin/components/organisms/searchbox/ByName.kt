package components.organisms.searchbox

import components.molecules.titleChips
import components.molecules.typesChips
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.textfield.textField
import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.model.toIdolName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import react.dom.form
import react.functionalComponent
import react.useEffect
import react.useState
import utilities.inputTarget
import utilities.invoke
import utilities.useDebounceEffect
import utilities.useTranslation

val SearchByNameComponent = functionalComponent<SearchByNameProps> { props ->
    val classes = useStyle()
    val (t, _) = useTranslation()

    val (idolName, setIdolName) = useState("")

    fun onChangeIdolName(value: String?) = props.onChangeSearchParams(props.params.change(value.toIdolName()))
    fun onSelectTitle(brand: Brands, checked: Boolean) = props.onChangeSearchParams(props.params.change(if (checked) brand else null))
    fun onSelectType(type: Types, checked: Boolean) = props.onChangeSearchParams(props.params.change(type, checked))

    useEffect(listOf(props.idolName)) { setIdolName(props.idolName ?: "") }
    useDebounceEffect(idolName, DEBOUNCE_TIMEOUT_MILLS, effect = ::onChangeIdolName)

    list {
        listItem {
            form(classes = classes.form) {
                textField {
                    attrs {
                        classes(classes.textField)
                        label { +t("searchBox.attributes.idolName") }
                        variant = FormControlVariant.outlined
                        value = idolName
                        onChangeFunction = { e -> setIdolName(e.inputTarget().value) }
                    }
                }
            }
        }

        listItem {
            titleChips {
                attrs {
                    title = props.params.brands
                    onSelect = ::onSelectTitle
                }
            }
        }

        listItem {
            typesChips(props.params.brands) {
                attrs {
                    types = props.params.types
                    onSelect = ::onSelectType
                }
            }
        }
    }
}

external interface SearchByNameProps : IdolSearchBoxProps<SearchParams.ByName>

private val SearchByNameProps.idolName get() = params.idolName?.value
