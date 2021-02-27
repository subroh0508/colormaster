package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.material.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawerHeader(title: String, subtext: String) {
    Spacer(Modifier.height(24.dp))
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, bottom = 18.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.height(36.dp)
                .wrapContentHeight(Alignment.Bottom),
        )
        Text(
            text = subtext,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.height(20.dp)
                .wrapContentHeight(Alignment.Bottom),
        )
    }
    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = .12F))
}
