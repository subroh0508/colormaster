package net.subroh0508.colormaster.common.extensions

import androidx.compose.ui.graphics.Color

fun Triple<Int, Int, Int>.toColor() = let { (r, g, b) -> Color(r, g, b) }
