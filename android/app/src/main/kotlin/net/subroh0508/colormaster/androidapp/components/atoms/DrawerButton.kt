package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.ButtonConstants
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun DrawerButton(
    asset: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        colors = ButtonConstants.defaultTextButtonColors(
            contentColor = MaterialTheme.colors.onSurface,
        ),
        modifier = Modifier.fillMaxWidth()
            .preferredHeight(48.dp),
    ) {
        Spacer(Modifier.preferredWidth(16.dp))
        Icon(asset)
        Spacer(Modifier.preferredWidth(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
