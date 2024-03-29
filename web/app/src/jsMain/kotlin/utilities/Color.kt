package utilities

import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.rgba

fun CSSColorValue.alpha(alpha: Number): CSSColorValue {
    val (red, green, blue) = parseToRGB()

    return rgba(red, green, blue, alpha)
}

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

fun CSSColorValue.parseToRGB(): List<Int> {
    when (this) {
        Color.white -> return listOf(255, 255, 255)
        Color.black -> return listOf(0, 0, 0)
    }

    val strColor = toString()
    if (strColor.startsWith("#")) {
        return listOf(
            strColor.substring(1..2).toIntOrNull(16) ?: 0,
            strColor.substring(3..4).toIntOrNull(16) ?: 0,
            strColor.substring(5..6).toIntOrNull(16) ?: 0,
        )
    }

    return strColor.let {
        """rgba?\(([0-9, ]+)\)""".toRegex().find(it)?.groupValues?.get(1)?.split(",") ?: listOf()
    }.map { it.trim().toIntOrNull() ?: 0 }
}
