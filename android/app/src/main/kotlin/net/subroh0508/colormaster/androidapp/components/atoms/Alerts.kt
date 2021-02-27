package net.subroh0508.colormaster.androidapp.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.themes.*

@Composable
fun InfoAlert(
    message: String,
    modifier: Modifier = Modifier,
    endAsset: ImageVector? = null,
    onClickEndIcon: () -> Unit = {},
) = Alert(Icons.Outlined.Info, message, blue500, modifier, endAsset, onClickEndIcon)
@Composable
fun SuccessAlert(
    message: String,
    modifier: Modifier = Modifier,
    endAsset: ImageVector? = null,
    onClickEndIcon: () -> Unit = {},
) = Alert(Icons.Outlined.CheckCircle, message, green500, modifier, endAsset, onClickEndIcon)
@Composable
fun WarningAlert(
    message: String,
    modifier: Modifier = Modifier,
    endAsset: ImageVector? = null,
    onClickEndIcon: () -> Unit = {},
) = Alert(Icons.Outlined.Warning, message, orange500, modifier, endAsset, onClickEndIcon)
@Composable
fun ErrorAlert(
    message: String,
    modifier: Modifier = Modifier,
    endAsset: ImageVector? = null,
    onClickEndIcon: () -> Unit = {},
) = Alert(painterResource(R.drawable.ic_error_outline_24dp), message, red500, modifier, endAsset, onClickEndIcon)

@Composable
fun Alert(
    painter: Painter,
    message: String,
    color: Color,
    modifier: Modifier = Modifier,
    endAsset: ImageVector? = null,
    onClickEndIcon: () -> Unit = {},
) = Alert(
    painter = painter,
    message = message,
    color = color,
    modifier = modifier,
    endPainter = endAsset?.let { rememberVectorPainter(it) },
    onClickEndIcon = onClickEndIcon,
)

@Composable
fun Alert(
    asset: ImageVector,
    message: String,
    color: Color,
    modifier: Modifier = Modifier,
    endAsset: ImageVector? = null,
    onClickEndIcon: () -> Unit = {},
) = Alert(
    painter = rememberVectorPainter(asset),
    message = message,
    color = color,
    modifier = modifier,
    endPainter = endAsset?.let { rememberVectorPainter(it) },
    onClickEndIcon = onClickEndIcon,
)

@Composable
fun Alert(
    painter: Painter,
    message: String,
    color: Color,
    modifier: Modifier = Modifier,
    endPainter: Painter? = null,
    onClickEndIcon: () -> Unit = {},
) {
    Card(
        elevation = 0.dp,
        contentColor = textColor(color),
        backgroundColor = backgroundColor(color),
        modifier = modifier,
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .align(Alignment.CenterVertically),
            )
            Text(
                message,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .weight(1.0F, true)
                    .align(Alignment.CenterVertically),
            )

            if (endPainter != null) {
                Icon(
                    painter = endPainter,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .align(Alignment.CenterVertically)
                        .clickable(onClick = onClickEndIcon),
                )
            }
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
        val modifier = Modifier
            .width(360.dp)
            .height(56.dp)
            .padding(8.dp)

        Column(Modifier.background(lightBackground)) {
            InfoAlert("Sample Text", endAsset = Icons.Default.KeyboardArrowDown, modifier = modifier)
            SuccessAlert("Sample Text", endAsset = Icons.Default.KeyboardArrowDown, modifier = modifier)
            WarningAlert("Sample Text", endAsset = Icons.Default.KeyboardArrowDown, modifier = modifier)
            ErrorAlert("Sample Text", endAsset = Icons.Default.KeyboardArrowDown, modifier = modifier)
        }
    }
}

@Preview
@Composable
fun PreviewAlerts_Dark() {
    ColorMasterTheme(darkTheme = true) {
        val modifier = Modifier
            .width(360.dp)
            .height(56.dp)
            .padding(8.dp)

        Column(Modifier.background(darkBackground)) {
            InfoAlert("Sample Text", endAsset = Icons.Default.KeyboardArrowDown, modifier = modifier)
            SuccessAlert("Sample Text", endAsset = Icons.Default.KeyboardArrowDown, modifier = modifier)
            WarningAlert("Sample Text", endAsset = Icons.Default.KeyboardArrowDown, modifier = modifier)
            ErrorAlert("Sample Text", endAsset = Icons.Default.KeyboardArrowDown, modifier = modifier)
        }
    }
}

