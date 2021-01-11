package components.atoms

import kotlinx.css.pct
import kotlinx.css.width
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.menuitem.menuItem
import materialui.components.paper.PaperProps
import materialui.components.paper.paper
import materialui.components.textfield.textField
import materialui.styles.makeStyles
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.div
import utilities.*

private const val AUTO_COMPLETE_TEXT_DEBOUNCE_TIMEOUT_MILLS = 500L

@Suppress("FunctionName")
fun <T: Any> AutoCompleteTextFieldComponent() = functionalComponent<AutoCompleteTextFieldProps<T>> { props ->
    val classes = useStyle()
    val (t, _) = useTranslation()

    val (suggestsQuery, setSuggestsQuery) = useState("")

    useEffect(listOf()) { setSuggestsQuery(props.query ?: "") }
    useDebounceEffect(suggestsQuery, AUTO_COMPLETE_TEXT_DEBOUNCE_TIMEOUT_MILLS) { props.onQueryChange(it) }

    autoSuggest<T> {
        attrs {
            suggestions = props.suggestions.toTypedArray()
            getSuggestionValue = props.getSuggestionValue
            renderSuggestionsContainer = { option ->
                renderSuggestionContainer(option.containerProps, option.children)
            }
            renderSuggestion = { suggestion, option ->
                renderSuggestion(props.getSuggestionValue(suggestion), option.isHighlighted)
            }
            renderInputComponent = { renderInputComponent(classes, t, it) }
            inputProps {
                value = suggestsQuery
                onChange = { e, option ->
                    if (e.target is HTMLInputElement) {
                        setSuggestsQuery(option.newValue ?: "")
                    }
                }
            }
            onSuggestionsFetchRequested = { }
            onSuggestionsClearRequested = { }
            onSuggestionSelected = { _, options -> props.onSuggestionSelected(options.suggestion) }
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
    suggestionValue: String,
    isHighlighted: Boolean,
) = buildElement {
    menuItem(button = false) {
        attrs {
            selected = isHighlighted
        }

        div { +suggestionValue }
    }
}

private fun renderInputComponent(
    classes: AutoCompleteTextFieldStyle,
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

external interface AutoCompleteTextFieldProps<T: Any> : RProps {
    var suggestions: List<T>
    var query: String?
    var getSuggestionValue: (T) -> String
    var onQueryChange: (String) -> Unit
    var onSuggestionSelected: (T) -> Unit
}

private external interface AutoCompleteTextFieldStyle {
    val textField: String
}

private val useStyle = makeStyles<AutoCompleteTextFieldStyle> {
    "textField" {
        width = 100.pct
    }
}
