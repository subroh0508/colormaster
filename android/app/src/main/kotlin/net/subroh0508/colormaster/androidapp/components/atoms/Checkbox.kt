package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme
import net.subroh0508.colormaster.androidapp.themes.darkBackground
import net.subroh0508.colormaster.androidapp.themes.lightBackground
import androidx.compose.material.Checkbox as ComposeCheckbox

@Composable
fun Checkbox(
    checked: Boolean,
    label: String? = null,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    if (label == null) {
        ComposeCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            checkedColor = MaterialTheme.colors.primary,
        )

        return
    }

    Row {
        ComposeCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            checkedColor = MaterialTheme.colors.primary,
            modifier = Modifier.preferredSize(42.dp),
        )
        Text(
            label,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@Preview
@Composable
fun CheckboxPreview_Light() {
    ColorMasterTheme(darkTheme = false) {
        Row(Modifier.background(color = lightBackground)) {
            Checkbox(label = "Selected", checked = true, onCheckedChange = {})
            Checkbox(label = "Not Selected", checked = false, onCheckedChange = {})
        }
    }
}

@Preview
@Composable
fun CheckboxPreview_Dark() {
    ColorMasterTheme(darkTheme = true) {
        Row(Modifier.background(color = darkBackground)) {
            Checkbox(label = "Selected", checked = true, onCheckedChange = {})
            Checkbox(label = "Not Selected", checked = false, onCheckedChange = {})
        }
    }
}
