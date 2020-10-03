package net.subroh0508.colormaster.androidapp.ui

import androidx.compose.ui.graphics.Color

val teal200 = Color(0xFF03DAC5)

val orange200 = Color(0xFFFFCC80)
val orange900 = Color(0xFFE65100)

val deepOrange200 = Color(0xFFFFAB91)
val deepOrange900 = Color(0xFFE65100)

val gray800 = Color(0xFF424242)
val gray900 = Color(0xFF212121)

val lightBackground = Color(0xFFFAFAFA)
val darkBackground = Color(0xFF121212)

fun String.hexToColor() = android.graphics.Color.parseColor(this).let { hex ->
    Color(
            red = (hex and 0xFF0000) shr 16,
            green = (hex and 0xFF00) shr 8,
            blue = (hex and 0xFF),
    )
}