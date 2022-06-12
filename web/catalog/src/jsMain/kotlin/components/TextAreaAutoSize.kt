package components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.TextArea
import org.jetbrains.compose.web.events.SyntheticChangeEvent
import org.w3c.dom.HTMLTextAreaElement

@Composable
fun TextAreaAutoSize(
    label: String,
    value: String?,
    onChange: (SyntheticChangeEvent<String, HTMLTextAreaElement>) -> Unit = {},
    applyAttrs: (AttrsScope<HTMLTextAreaElement>.() -> Unit)? = null,
) {
    val classNames = remember { mutableStateOf(arrayOf<String>()) }
    val dummyText = remember(label, value) { mutableStateOf(value ?: label) }
    val textAreaHeight = remember { mutableStateOf(0) }

    ActualTextArea(value, classNames, dummyText, onChange) {
        applyAttrs?.invoke(this)
        style {
            height(textAreaHeight.value.px)
            property("resize", "none")
        }
    }
    ShadowTextArea(label, textAreaHeight, dummyText.value, classNames.value)
}

@Composable
private fun ActualTextArea(
    value: String?,
    classNames: MutableState<Array<String>>,
    state: MutableState<String>,
    onChange: (SyntheticChangeEvent<String, HTMLTextAreaElement>) -> Unit,
    applyAttrs: (AttrsScope<HTMLTextAreaElement>.() -> Unit)?,
) = TextArea {
    applyAttrs?.invoke(this)
    style { overflow("hidden") }

    value?.let { value(it) }
    onInput {
        state.value = it.value
        onChange(onChange)
    }

    ref {
        classNames.value = it.className.split(" ").toTypedArray()

        onDispose { classNames.value = arrayOf() }
    }
}

@OptIn(ExperimentalComposeWebApi::class)
@Composable
private fun ShadowTextArea(
    label: String,
    textAreaHeight: MutableState<Int>,
    value: String?,
    classNames: Array<String>,
) {
    var element by remember { mutableStateOf<HTMLTextAreaElement?>(null) }

    LaunchedEffect(label, value) {
        element?.value = (value ?: label)
        textAreaHeight.value = element?.scrollHeight ?: 0
    }

    TextArea {
        classes(*classNames)
        style {
            position(Position.Absolute)
            overflow("hidden")
            height(0.px)
            minHeight(0.px)
            top(0.px)
            left(0.px)
            transform { translateZ(0.px) }
            readOnly()
        }

        ref {
            element = it
            onDispose { element = null }
        }
    }
}
