package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.model.HexColor

@Composable
fun ColorItemContent(
    label: String,
    color: HexColor,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            label,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, start = 24.dp, end = 24.dp),
        )
        Text(
            "#${color.value}",
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp, start = 24.dp, end = 24.dp),
        )
    }
}
