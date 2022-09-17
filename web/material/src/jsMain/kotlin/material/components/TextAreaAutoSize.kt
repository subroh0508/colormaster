package material.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.TextArea
import org.jetbrains.compose.web.events.SyntheticInputEvent
import org.w3c.dom.HTMLTextAreaElement

@Composable
fun TextAreaAutoSize(
    label: String,
    value: String?,
    hasLeading: Boolean = false,
    hasTrailing: Boolean = false,
    onChange: (SyntheticInputEvent<String, HTMLTextAreaElement>) -> Unit = {},
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
    ShadowTextArea(label, textAreaHeight, dummyText.value, classNames.value, hasLeading, hasTrailing)
}

@Composable
private fun ActualTextArea(
    value: String?,
    classNames: MutableState<Array<String>>,
    state: MutableState<String>,
    onChange: (SyntheticInputEvent<String, HTMLTextAreaElement>) -> Unit,
    applyAttrs: (AttrsScope<HTMLTextAreaElement>.() -> Unit)?,
) = TextArea {
    applyAttrs?.invoke(this)
    style { overflow("hidden") }

    value(value ?: "")
    onInput {
        state.value = it.value
        onChange(it)
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
    hasLeading: Boolean = false,
    hasTrailing: Boolean = false,
) {
    var element by remember { mutableStateOf<HTMLTextAreaElement?>(null) }

    LaunchedEffect(label, value, hasLeading, hasTrailing) {
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

            val width = listOf(
                hasLeading,
                hasTrailing,
            ).fold<Boolean, CSSNumeric>(100.percent) { acc, b ->
                if (!b) return@fold acc

                acc - 48.px
            }
            width(width)
        }
        readOnly()

        ref {
            element = it
            onDispose { element = null }
        }
    }
}
