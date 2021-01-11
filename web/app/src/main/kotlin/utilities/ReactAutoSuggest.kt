package utilities

import kotlinext.js.jsObject
import org.w3c.dom.events.Event
import react.*

@JsModule("react-autosuggest")
private external val AutoSuggest: RClass<AutoSuggestProps<*>>

fun <T: Any> RBuilder.autoSuggest(handler: RHandler<AutoSuggestProps<T>>) = AutoSuggest {
    @Suppress("UNCHECKED_CAST")
    handler(this as RElementBuilder<AutoSuggestProps<T>>)
}

external interface AutoSuggestProps<T: Any> : RProps {
    var suggestions: Array<T>
    var alwaysRenderSuggestions: Boolean
    var onSuggestionsFetchRequested: (T?) -> Unit
    var onSuggestionsClearRequested: () -> Unit
    var getSuggestionValue: (T) -> String
    var inputProps: AutoSuggestInputProps
    var renderSuggestion: (T, RenderSuggestionOptions) -> ReactElement
    var renderSuggestionsContainer: (RenderSuggestionsContainerOptions) -> ReactElement
    var renderInputComponent: (AutoSuggestInputProps) -> ReactElement
    var onSuggestionSelected: (Event, OnSuggestionSelectedOptions<T>) -> Unit
}

fun <T: Any> AutoSuggestProps<T>.inputProps(block: AutoSuggestInputProps.() -> Unit)  {
    inputProps = jsObject(block)
}

external interface AutoSuggestInputProps : RProps  {
    var placeholder: String?
    var value: Any?
    var autoComplete: String
    var margin: String?
    var onChange: (Event, OnChangeOptions) -> Unit
}

fun <T> AutoSuggestInputProps.inputRef(ref: (T?) -> Unit) {
    asDynamic().inputRef = ref
}

external interface RenderSuggestionOptions {
    val query: String
    val isHighlighted: Boolean
}

external interface RenderSuggestionsContainerOptions : RProps {
    val containerProps: RProps
    val query: String
    val children: Any?
}

external interface OnChangeOptions {
    val newValue: String?
    val method: String
}

external interface OnSuggestionSelectedOptions<out T: Any> {
    val suggestion: T
    val suggestionValue: String
    val suggestionIndex: Int
    val sectionIndex: Int?
    val method: String
}
