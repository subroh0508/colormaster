package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme
import net.subroh0508.colormaster.androidapp.themes.captionTextStyle

private const val DEBOUNCE_TIME_MILLIS = 500L

@Composable
fun DebounceTextField(
    text: String? = null,
    label: String = "",
    debounceTimeMillis: Long = DEBOUNCE_TIME_MILLIS,
    onTextChanged: (String?) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var textFieldState by remember { mutableStateOf(TextFieldValue()) }
    val debounceFlowState by remember { mutableStateOf(MutableStateFlow("")) }

    onCommit(text) {
        if (text == textFieldState.text) return@onCommit

        textFieldState = TextFieldValue(text = text ?: "")
    }

    LaunchedEffect(onTextChanged) {
        debounceFlowState.debounce(debounceTimeMillis)
            .collect { onTextChanged(it.takeIf(String::isNotBlank)) }
    }

    OutlinedTextField(
        value = textFieldState,
        label = { Text(label, style = captionTextStyle) },
        onValueChange = {
            textFieldState = it
            debounceFlowState.value = textFieldState.text
        },
        // Do not become onSurface without overriding the character color
        textStyle = MaterialTheme.typography.body1.copy(
            MaterialTheme.colors.onSurface,
        ),
        modifier = modifier,
    )
}

@Composable
fun DebounceTextField(
    text: String? = null,
    @StringRes labelRes: Int? = null,
    debounceTimeMillis: Long = DEBOUNCE_TIME_MILLIS,
    onTextChanged: (String?) -> Unit = {},
    modifier: Modifier = Modifier,
) = DebounceTextField(
    text = text,
    label = labelRes?.let { stringResource(it) } ?: "",
    debounceTimeMillis = debounceTimeMillis,
    onTextChanged = onTextChanged,
    modifier = modifier,
)

@Preview
@Composable
fun TextFieldPreview_Light() {
    val (text, setText) = remember { mutableStateOf("") }

    ColorMasterTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colors.background) {
            DebounceTextField(
                text = text,
                label = "文字列",
                onTextChanged = { setText(it ?: "") },
            )
        }
    }
}

@Preview
@Composable
fun TextFieldPreview_Dark() {
    val (text, setText) = remember { mutableStateOf("") }

    ColorMasterTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colors.background) {
            DebounceTextField(
                text = text,
                label = "文字列",
                onTextChanged = { setText(it ?: "") },
            )
        }
    }
}
