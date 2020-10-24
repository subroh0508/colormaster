package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme
import net.subroh0508.colormaster.androidapp.themes.captionTextStyle

@Composable
fun TextField(
    text: String? = null,
    label: String = "",
    onTextChanged: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var textFieldState by remember { mutableStateOf(TextFieldValue()) }

    onCommit(text) { textFieldState = TextFieldValue(text = text ?: "") }

    OutlinedTextField(
        value = textFieldState,
        label = { Text(label, style = captionTextStyle) },
        onValueChange = {
            textFieldState = it
            onTextChanged(textFieldState.text)
        },
        textStyle = MaterialTheme.typography.body1,
        modifier = modifier,
    )
}

@Composable
fun TextField(
    text: String? = null,
    @StringRes labelRes: Int? = null,
    onTextChanged: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) = TextField(
    text = text,
    label = labelRes?.let { stringResource(it) } ?: "",
    onTextChanged = onTextChanged,
    modifier = modifier,
)

@Preview
@Composable
fun TextFieldPreview_Light() {
    val (text, setText) = remember { mutableStateOf("") }

    ColorMasterTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colors.background) {
            TextField(
                text = text,
                label = "文字列",
                onTextChanged = { setText(it) },
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
            TextField(
                text = text,
                label = "文字列",
                onTextChanged = { setText(it) },
            )
        }
    }
}
