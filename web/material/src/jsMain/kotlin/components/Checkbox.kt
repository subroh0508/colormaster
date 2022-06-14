package components

import androidx.compose.runtime.*
import externals.MDCCheckbox
import externals.MDCFormField
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.svg.Path
import org.jetbrains.compose.web.svg.Svg
import org.w3c.dom.HTMLElement

private const val CHECKBOX_ID = "checkbox"
private const val CHECKMARK_PATH = "M1.73,12.91 8.1,19.28 22.79,4.59"

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun Checkbox(
    label: String,
    value: Boolean = false,
    id: String = CHECKBOX_ID,
) {
    var checkboxElement by remember { mutableStateOf<HTMLElement?>(null) }
    var formFieldElement by remember { mutableStateOf<HTMLElement?>(null) }

    var checked by remember { mutableStateOf(value) }

    SideEffect {
        val checkbox = checkboxElement?.let { MDCCheckbox(it).checked = value }
        formFieldElement?.let { MDCFormField(it).input = checkbox }
    }

    Div({
        classes("mdc-form-field")
        ref {
            formFieldElement = it
            onDispose { formFieldElement = null }
        }
    }) {
        Div({
            classes("mdc-checkbox")
            ref {
                checkboxElement = it
                onDispose { checkboxElement = null }
            }
        }) {
            Input(InputType.Checkbox) {
                classes("mdc-checkbox__native-control")
                id(id)
                onInput { checked = it.value }
            }
            Div({ classes("mdc-checkbox__background") }) {
                Svg("0 0 24 24", { classes("mdc-checkbox__checkmark") }) {
                    Path(CHECKMARK_PATH, {
                        classes("mdc-checkbox__checkmark-path")
                        attr("fill", "none")
                    })
                }
                Div({ classes("mdc-checkbox__mixedmark") })
            }
            Div({
                classes("mdc-checkbox__ripple")
            })
            Div({ classes("mdc-checkbox__focus-ring") })
        }
        Label(CHECKBOX_ID) { Text(label) }
    }
}
