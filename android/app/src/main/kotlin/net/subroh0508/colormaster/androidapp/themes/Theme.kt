package net.subroh0508.colormaster.androidapp.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import net.subroh0508.colormaster.androidapp.ColorMasterApplication

private val DarkColorPalette = darkColors(
        primary = orange200,
        primaryVariant = deepOrange200,
        secondary = teal200,
        surface = gray900,
        background = darkBackground,
        onPrimary = Color.Black,
)

private val LightColorPalette = lightColors(
        primary = orange900,
        primaryVariant = deepOrange900,
        secondary = teal200,
        surface = Color.White,
        background = lightBackground,
        onPrimary = Color.White,

        /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun ColorMasterTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
