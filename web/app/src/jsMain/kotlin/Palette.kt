import org.jetbrains.compose.web.css.rgba
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

sealed class Palette {
    fun primary(alpha: Number = 1) = color(primary, alpha)
    fun secondary(alpha: Number = 1) = color(secondary, alpha)
    fun surface(alpha: Number = 1) = color(surface, alpha)
    fun background(alpha: Number = 1) = color(background, alpha)

    fun onPrimary(alpha: Number = 1) = color(onPrimary, alpha)
    fun onSecondary(alpha: Number = 1) = color(onSecondary, alpha)
    fun onSurface(alpha: Number = 1) = color(onSurface, alpha)

    fun textPrimary(alpha: Number = 1) = color(textPrimary, alpha)
    fun textSecondary(alpha: Number = 1) = color(textSecondary, alpha)
    fun textLink(alpha: Number = 1) = color(textLink, alpha)

    private fun color(rgb: RGB, alpha: Number) = rgb.let { (r, g, b) -> rgba(r, g, b, alpha) }

    protected abstract val primary: RGB
    protected abstract val secondary: RGB
    protected abstract val surface: RGB
    protected abstract val background: RGB

    protected abstract val onPrimary: RGB
    protected abstract val onSecondary: RGB
    protected abstract val onSurface: RGB

    protected abstract val textPrimary: RGB
    protected abstract val textSecondary: RGB
    protected abstract val textLink: RGB

    data class RGB(val red: Int, val green: Int, val blue: Int)

    data class Light(
        override val primary: RGB = RGB(230, 81, 0),
        override val secondary: RGB = RGB(255, 240, 159),
        override val surface: RGB = White,
        override val background: RGB = RGB(250, 250, 250),
        override val onPrimary: RGB = White,
        override val onSecondary: RGB = White,
        override val onSurface: RGB = Black,
        override val textPrimary: RGB = Black,
        override val textSecondary: RGB = Black,
        override val textLink: RGB = RGB(0, 181, 226),
    ) : Palette()

    data class Dark(
        override val primary: RGB = RGB(255, 204, 128),
        override val secondary: RGB = RGB(255, 240, 159),
        override val surface: RGB = RGB(48, 48, 48),
        override val background: RGB = RGB(18, 18, 18),
        override val onPrimary: RGB = Black,
        override val onSecondary: RGB = Black,
        override val onSurface: RGB = White,
        override val textPrimary: RGB = White,
        override val textSecondary: RGB = White,
        override val textLink: RGB = RGB(0, 181, 226),
    ) : Palette()

    sealed class Alert {
        companion object {
            fun light(
                info: RGB = RGB(79, 195, 247),
                success: RGB = RGB(129, 199, 132),
                warning: RGB = RGB(255, 183, 77),
                error: RGB = RGB(229, 115, 115),
            ) = Light(info, success, warning, error)

            fun dark(
                info: RGB = RGB(41, 182, 246),
                success: RGB = RGB(102, 187, 106),
                warning: RGB = RGB(255, 167, 38),
                error: RGB = RGB(244, 67, 54),
            ) = Dark(info, success, warning, error)
        }

        abstract val info: RGB
        abstract val success: RGB
        abstract val warning: RGB
        abstract val error: RGB

        fun info(alpha: Number = 1) = color(info, alpha)
        fun success(alpha: Number = 1) = color(success, alpha)
        fun warning(alpha: Number = 1) = color(warning, alpha)
        fun error(alpha: Number = 1) = color(error, alpha)

        private fun color(rgb: RGB, alpha: Number) = rgb.let { (r, g, b) -> rgba(r, g, b, alpha) }

        data class Light(
            override val info: RGB,
            override val success: RGB,
            override val warning: RGB,
            override val error: RGB,
        ) : Alert()

        data class Dark(
            override val info: RGB,
            override val success: RGB,
            override val warning: RGB,
            override val error: RGB,
        ) : Alert()
    }

    companion object {
        val White = RGB(255, 255, 255)
        val Black = RGB(0, 0, 0)
    }
}
