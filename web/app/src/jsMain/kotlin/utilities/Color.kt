package utilities

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.rgb

fun CSSColorValue.lighten(coefficient: Number): CSSColorValue {
    val (red, green, blue) = parseToRGB()

    return rgb(
        (red + (255 - red) * coefficient.toFloat()).toInt(),
        (green + (255 - green) * coefficient.toFloat()).toInt(),
        (blue + (255 - blue) * coefficient.toFloat()).toInt(),
    )
}

fun CSSColorValue.darken(coefficient: Number): CSSColorValue {
    val (red, green, blue) = parseToRGB()

    return rgb(
        (red * (1 - coefficient.toFloat())).toInt(),
        (green * (1 - coefficient.toFloat())).toInt(),
        (blue * (1 - coefficient.toFloat())).toInt(),
    )
}

private fun CSSColorValue.parseToRGB() = toString().let {
    """rgba?\(([0-9, ]+)\)""".toRegex().find(it)?.groupValues?.get(1)?.split(",") ?: listOf()
}.map { it.trim().toIntOrNull() ?: 0 }
