package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.model.IntColor
import net.subroh0508.colormaster.model.isBrighter
import net.subroh0508.colormaster.model.toHex

@Composable
fun ColorItemContent(
    label: String,
    intColor: IntColor,
    modifier: Modifier = Modifier,
) {
    val textColor = if (intColor.isBrighter) Color.Black else Color.White

    Column(modifier) {
        Text(
            label,
            color = textColor,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, start = 24.dp, end = 24.dp),
        )
        Text(
            intColor.toHex(),
            color = textColor,
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp, start = 24.dp, end = 24.dp),
        )
    }
}
