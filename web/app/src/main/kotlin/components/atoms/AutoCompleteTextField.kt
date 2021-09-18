package components.atoms

import kotlinx.css.*
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.menuitem.menuItem
import materialui.components.paper.PaperProps
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.textfield.textField
import materialui.styles.makeStyles
import org.w3c.dom.HTMLTextAreaElement
import react.*
import react.dom.attrs
import react.dom.span
import utilities.*

private const val AUTO_COMPLETE_TEXT_DEBOUNCE_TIMEOUT_MILLS = 500L

@Suppress("FunctionName")
fun <T: Any> AutoCompleteTextFieldComponent() = functionComponent<AutoCompleteTextFieldProps<T>> { props ->
    val (suggestsQuery, setSuggestsQuery) = useState("")

    useEffect(props.query) { setSuggestsQuery(props.query ?: "") }
    useDebounceEffect(suggestsQuery, AUTO_COMPLETE_TEXT_DEBOUNCE_TIMEOUT_MILLS) { props.onQueryChange(it) }

    autoSuggest<T> {
        attrs {
            suggestions = props.suggestions.toTypedArray()
            getSuggestionValue = props.getSuggestionValue
            renderSuggestionsContainer = { option ->
                renderSuggestionContainer {
                    attrs.containerProps = option.containerProps
                    attrs.children = option.children
                }
            }
            renderSuggestion = { suggestion, option ->
                renderSuggestion {
                    attrs.value = props.getSuggestionValue(suggestion)
                    attrs.isHighlighted = option.isHighlighted
                }
            }
            renderInputComponent = {
                renderInputComponent {
                    attrs.inputProps = it
                }
            }
            inputProps {
                value = suggestsQuery
                onChange = { e, option ->
                    if (e.target is HTMLTextAreaElement) {
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

external interface AutoCompleteTextFieldProps<T: Any> : RProps {
    var suggestions: List<T>
    var query: String?
    var getSuggestionValue: (T) -> String
    var onQueryChange: (String) -> Unit
    var onSuggestionSelected: (T) -> Unit
}

private fun renderSuggestionContainer(handler: RHandler<SuggestionContainerProps>) = buildElement {
    child(SuggestionContainerComponent, handler = handler)
}

private val SuggestionContainerComponent = functionComponent<SuggestionContainerProps> { props ->
    val classes = useSuggestionContainerStyle()

    paper(PaperStyle.root to classes.root) {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        props(props.containerProps as PaperProps)
        attrs.square = true

        childList.addAll(Children.toArray(props.children))
    }
}

private external interface SuggestionContainerProps : RProps {
    var containerProps: RProps
}

private external interface SuggestionContainerStyle {
    val root: String
}

private val useSuggestionContainerStyle = makeStyles<SuggestionContainerStyle> {
    "root" {
        zIndex = 1
        descendants("ul") {
            listStyleType = ListStyleType.none
            padding(left = 0.px)
        }
    }
}

private fun renderSuggestion(handler: RHandler<SuggestionProps>) = buildElement {
    child(SuggestionComponent, handler = handler)
}

private val SuggestionComponent = functionComponent<SuggestionProps> { props ->
    val classes = useSuggestionStyle()

    menuItem(button = false) {
        attrs {
            selected = props.isHighlighted
        }

        span(classes = classes.text) { +props.value }
    }
}

private external interface SuggestionProps : RProps {
    var value: String
    var isHighlighted: Boolean
}

private external interface SuggestionStyle {
    val text: String
}

private val useSuggestionStyle = makeStyles<SuggestionStyle> {
    "text" {
        width = 100.pct
        wordBreak = WordBreak.breakWord
        whiteSpace = WhiteSpace.preWrap
    }
}

private fun renderInputComponent(handler: RHandler<AutoCompleteInputProps>) = buildElement {
    child(AutoCompleteInputComponent, handler = handler)
}

private val AutoCompleteInputComponent = functionComponent<AutoCompleteInputProps> { props ->
    val classes = useAutoCompleteInputStyle()
    val (t, _) = useTranslation()

    textField {
        attrs {
            classes(classes.root)
            label { +t("searchBox.attributes.liveName") }
            multiline = true
            variant = FormControlVariant.outlined
            InputProps = props.inputProps
        }
    }
}

private external interface AutoCompleteInputProps : RProps {
    var inputProps: AutoSuggestInputProps
}

private external interface AutoCompleteInputStyle {
    val root: String
}

private val useAutoCompleteInputStyle = makeStyles<AutoCompleteInputStyle> {
    "root" {
        width = 100.pct
    }
}
