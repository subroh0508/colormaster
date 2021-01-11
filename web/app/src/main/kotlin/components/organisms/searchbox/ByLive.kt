package components.organisms.searchbox

import components.atoms.AutoCompleteTextFieldComponent
import components.atoms.AutoCompleteTextFieldProps
import materialui.components.list.list
import materialui.components.listitem.listItem
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import react.*
import react.dom.form

val SearchByLiveComponent = functionalComponent<SearchByLiveProps> { props ->
    val classes = useStyle()

    fun onSelectLiveName(value: LiveName) = props.onChangeSearchParams(props.params.select(value))
    fun onClearLiveName() = props.onChangeSearchParams(props.params.clear())
    fun onChangeLiveNameQuery(value: String?) = props.onChangeSearchParams(props.params.change(value))

    list {
        listItem {
            form(classes = classes.form) {
                liveAutoComplete {
                    attrs {
                        suggestions = props.params.suggests
                        query = props.query
                        getSuggestionValue = LiveName::value
                        onQueryChange = ::onChangeLiveNameQuery
                        onSuggestionSelected = ::onSelectLiveName
                    }
                }
            }
        }
    }
}

private fun RBuilder.liveAutoComplete(handler: RHandler<AutoCompleteTextFieldProps<LiveName>>) = child(liveAutoCompleteComponent, handler = handler)

private val liveAutoCompleteComponent = AutoCompleteTextFieldComponent<LiveName>()

external interface SearchByLiveProps : IdolSearchBoxProps<SearchParams.ByLive>

private val SearchByLiveProps.query get() = params.liveName?.value ?: params.query

