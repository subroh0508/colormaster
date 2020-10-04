package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Tab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    var textModifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
    if (selected) {
        textModifier =
            Modifier.border(BorderStroke(2.dp, MaterialTheme.colors.onSurface), RoundedCornerShape(16.dp))
                .then(textModifier)
    }

    androidx.compose.material.Tab(
        selected = selected,
        onClick = onClick,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.button,
            modifier = textModifier,
        )
    }
}
