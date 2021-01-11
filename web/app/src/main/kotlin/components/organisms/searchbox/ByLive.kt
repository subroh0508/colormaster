package components.organisms.searchbox

import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.textfield.textField
import net.subroh0508.colormaster.model.toIdolName
import net.subroh0508.colormaster.model.toLiveName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import react.dom.form
import react.functionalComponent
import react.useEffect
import react.useState
import utilities.inputTarget
import utilities.invoke
import utilities.useDebounceEffect
import utilities.useTranslation

val SearchByLiveComponent = functionalComponent<SearchByLiveProps> { props ->
    val classes = useStyle()
    val (t, _) = useTranslation()

    val (suggestsQuery, setSuggestsQuery) = useState("")

    fun onSelectLiveName(value: String?) = props.onChangeSearchParams(
        value.toLiveName()?.let(props.params::select) ?: props.params.clear()
    )
    fun onChangeLiveNameQuery(value: String?) = props.onChangeSearchParams(props.params.change(value))

    useEffect(listOf(props.liveNameQuery)) { setSuggestsQuery(props.liveNameQuery ?: "") }
    useDebounceEffect(suggestsQuery, DEBOUNCE_TIMEOUT_MILLS, effect = ::onChangeLiveNameQuery)

    list {
        listItem {
            form(classes = classes.form) {
                textField {
                    attrs {
                        classes(classes.textField)
                        label { +t("searchBox.attributes.liveName") }
                        variant = FormControlVariant.outlined
                        value = suggestsQuery
                        onChangeFunction = { e -> setSuggestsQuery(e.inputTarget().value) }
                    }
                }
            }
        }
    }
}

external interface SearchByLiveProps : IdolSearchBoxProps<SearchParams.ByLive>

private val SearchByLiveProps.liveNameQuery get() = params.query

