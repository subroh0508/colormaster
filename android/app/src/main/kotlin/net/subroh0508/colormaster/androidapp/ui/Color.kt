package net.subroh0508.colormaster.androidapp.ui

import androidx.compose.ui.graphics.Color

val purple200 = Color(0xFFBB86FC)
val purple500 = Color(0xFF6200EE)
val purple700 = Color(0xFF3700B3)
val teal200 = Color(0xFF03DAC5)

fun String.hexToColor() = android.graphics.Color.parseColor(this).let { hex ->
    Color(
            red = (hex and 0xFF0000) shr 16,
            green = (hex and 0xFF00) shr 8,
            blue = (hex and 0xFF),
    )
}
