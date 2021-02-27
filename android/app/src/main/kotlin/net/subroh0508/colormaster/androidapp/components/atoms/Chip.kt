package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme
import net.subroh0508.colormaster.androidapp.themes.darkBackground
import net.subroh0508.colormaster.androidapp.themes.lightBackground

@Composable
fun Chip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit = {},
) {
    Text(
        label,
        color = if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
        modifier = ChipModifier(selected).clickable(onClick = onClick),
        style = MaterialTheme.typography.body2
    )
}

@Composable
private fun ChipModifier(selected: Boolean) = Modifier.height(32.dp)
        .background(
            color = if (selected) MaterialTheme.colors.primary else Color.Transparent,
            shape = CircleShape,
        )
        .border(BorderStroke(1.dp, MaterialTheme.colors.primary), CircleShape)
        .wrapContentHeight(Alignment.CenterVertically)
        .padding(horizontal = 12.dp)


@Preview
@Composable
fun PreviewChip_Light() {
    ColorMasterTheme(darkTheme = false) {
        Row(Modifier.background(color = lightBackground)) {
            Chip("Selected", true)
            Chip("Not Selected", false)
        }
    }
}

@Preview
@Composable
fun PreviewChip_Dark() {
    ColorMasterTheme(darkTheme = true) {
        Row(Modifier.background(color = darkBackground)) {
            Chip("Selected", true)
            Chip("Not Selected", false)
        }
    }
}
