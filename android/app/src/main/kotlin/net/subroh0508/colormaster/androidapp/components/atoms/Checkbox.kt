package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.material.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CheckboxConstants
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
    modifier: Modifier = Modifier,
) {
    if (label == null) {
        ComposeCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxConstants.defaultColors(
                checkedColor = MaterialTheme.colors.primary,
            ),
            modifier = modifier,
        )

        return
    }

    Row(modifier) {
        ComposeCheckbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxConstants.defaultColors(
                checkedColor = MaterialTheme.colors.primary,
            ),
            modifier = Modifier.padding(end = 9.dp),
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
            Checkbox(
                label = "Selected",
                checked = true,
                onCheckedChange = {},
                modifier = Modifier.padding(end = 9.dp),
            )
            Checkbox(
                label = "Not Selected",
                checked = false,
                onCheckedChange = {},
                modifier = Modifier.padding(start = 9.dp),
            )
        }
    }
}

@Preview
@Composable
fun CheckboxPreview_Dark() {
    ColorMasterTheme(darkTheme = true) {
        Row(Modifier.background(color = darkBackground)) {
            Checkbox(
                label = "Selected",
                checked = true,
                onCheckedChange = {},
                modifier = Modifier.padding(end = 9.dp),
            )
            Checkbox(
                label = "Not Selected",
                checked = false,
                onCheckedChange = {},
                modifier = Modifier.padding(start = 9.dp),
            )
        }
    }
}
