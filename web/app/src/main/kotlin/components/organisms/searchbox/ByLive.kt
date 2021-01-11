package components.organisms.searchbox

import kotlinext.js.Object
import kotlinext.js.jsObject
import kotlinx.html.js.onChangeFunction
import materialui.components.container.ContainerProps
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.input.InputProps
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.menuitem.menuItem
import materialui.components.paper.PaperProps
import materialui.components.paper.paper
import materialui.components.textfield.textField
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.model.toLiveName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.form
import utilities.*

val SearchByLiveComponent = functionalComponent<SearchByLiveProps> { props ->
    val classes = useStyle()
    val (t, _) = useTranslation()

    val (suggestsQuery, setSuggestsQuery) = useState("")

    fun onSelectLiveName(value: LiveName) = props.onChangeSearchParams(props.params.select(value))
    fun onClearLiveName() = props.onChangeSearchParams(props.params.clear())
    fun onChangeLiveNameQuery(value: String?) = props.onChangeSearchParams(props.params.change(value))

    useEffect(listOf()) { setSuggestsQuery(props.liveNameQuery ?: "") }
    useDebounceEffect(suggestsQuery, DEBOUNCE_TIMEOUT_MILLS, effect = ::onChangeLiveNameQuery)

    list {
        listItem {
            form(classes = classes.form) {
                autoSuggest<LiveName> {
                    attrs {
                        suggestions = props.params.suggests.toTypedArray()
                        getSuggestionValue = LiveName::value
                        renderSuggestionsContainer = { option ->
                            renderSuggestionContainer(option.containerProps, option.children)
                        }
                        renderSuggestion = { suggestion, option -> renderSuggestion(suggestion, option.isHighlighted) }
                        renderInputComponent = { renderInputComponent(classes, t, it) }
                        inputProps {
                            value = props.liveName ?: suggestsQuery
                            onChange = { e, option ->
                                if (e.target is HTMLInputElement) {
                                    setSuggestsQuery(option.newValue ?: "")
                                }
                            }
                        }
                        onSuggestionsFetchRequested = { }
                        onSuggestionsClearRequested = { }
                        onSuggestionSelected = { _, options -> onSelectLiveName(options.suggestion) }
                    }
                }
            }
        }
    }
}

private fun renderSuggestionContainer(
    containerProps: RProps,
    children: Any?,
) = buildElement {
    paper {
        @Suppress("UNCHECKED_CAST")
        props(containerProps as PaperProps)
        attrs.square = true

        childList.addAll(Children.toArray(children))
    }
}

private fun renderSuggestion(
    suggestion: LiveName,
    isHighlighted: Boolean,
) = buildElement {
    menuItem(button = false) {
        attrs {
            selected = isHighlighted
        }

        div { +suggestion.value }
    }
}

private fun renderInputComponent(
    classes: IdolSearchBoxStyle,
    t: I18nextText,
    inputProps: AutoSuggestInputProps,
) = buildElement {
    textField {
        attrs {
            classes(classes.textField)
            label { +t("searchBox.attributes.liveName") }
            variant = FormControlVariant.outlined
            InputProps = inputProps
        }
    }
}

external interface SearchByLiveProps : IdolSearchBoxProps<SearchParams.ByLive>

private val SearchByLiveProps.liveName get() = params.liveName?.value
private val SearchByLiveProps.liveNameQuery get() = params.query

