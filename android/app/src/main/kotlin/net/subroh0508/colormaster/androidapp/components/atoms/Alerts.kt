package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.themes.*

@Composable
fun InfoAlert(message: String, modifier: Modifier = Modifier) = Alert(Icons.Outlined.Info, message, blue500, modifier)
@Composable
fun SuccessAlert(message: String, modifier: Modifier = Modifier) = Alert(Icons.Outlined.CheckCircle, message, green500, modifier)
@Composable
fun WarningAlert(message: String, modifier: Modifier = Modifier) = Alert(Icons.Outlined.Warning, message, orange500, modifier)
@Composable
fun ErrorAlert(message: String, modifier: Modifier = Modifier) = Alert(vectorResource(R.drawable.ic_error_outline_24dp), message, red500, modifier)

@Composable
fun Alert(
    asset: VectorAsset,
    message: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = 0.dp,
        contentColor = textColor(color),
        backgroundColor = backgroundColor(color),
        modifier = modifier,
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Icon(
                asset = asset,
                tint = color,
                modifier = Modifier.padding(end = 12.dp),
            )
            Text(
                message,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
private fun textColor(color: Color) =
        if (MaterialTheme.colors.isLight)
            color.darken(0.6F)
        else
            color.lighten(0.6F)

@Composable
private fun backgroundColor(color: Color) =
        if (MaterialTheme.colors.isLight)
            color.lighten(0.9F)
        else
            color.darken(0.9F)


private fun Color.lighten(percent: Float) = Color(
    red + (1.0F - red) * percent,
    green + (1.0F - green) * percent,
    blue + (1.0F - blue) * percent,
    alpha,
)
private fun Color.darken(percent: Float) = Color(
    red * (1.0F - percent),
    green * (1.0F - percent),
    blue * (1.0F - percent),
    alpha,
)

@Preview
@Composable
fun PreviewAlerts_Light() {
    ColorMasterTheme(darkTheme = false) {
        val modifier = Modifier.padding(8.dp)

        Column(Modifier.background(lightBackground)) {
            InfoAlert("Sample Text", modifier)
            SuccessAlert("Sample Text", modifier)
            WarningAlert("Sample Text", modifier)
            ErrorAlert("Sample Text", modifier)
        }
    }
}

@Preview
@Composable
fun PreviewAlerts_Dark() {
    ColorMasterTheme(darkTheme = true) {
        val modifier = Modifier.padding(8.dp)

        Column(Modifier.background(darkBackground)) {
            InfoAlert("Sample Text", modifier)
            SuccessAlert("Sample Text", modifier)
            WarningAlert("Sample Text", modifier)
            ErrorAlert("Sample Text", modifier)
        }
    }
}

