package net.subroh0508.colormaster.presentation.common.extensions

import org.jetbrains.compose.web.css.rgb

fun Triple<Int, Int, Int>.toColor() = let { (r, g, b) -> rgb(r, g, b) }
