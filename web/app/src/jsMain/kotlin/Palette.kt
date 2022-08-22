import org.jetbrains.compose.web.css.CSSColorValue
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.css.rgba

sealed class Palette {
    abstract val primary: CSSColorValue
    abstract val secondary: CSSColorValue
    abstract val surface: CSSColorValue
    abstract val background: CSSColorValue
    abstract val divider: CSSColorValue

    abstract val onPrimary: CSSColorValue
    abstract val onSecondary: CSSColorValue
    abstract val onSurface: CSSColorValue

    abstract val textPrimary: CSSColorValue
    abstract val textSecondary: CSSColorValue
    abstract val textLink: CSSColorValue

    data class Light(
        override val primary: CSSColorValue = rgb(230, 81, 0),
        override val secondary: CSSColorValue = rgb(255, 240, 159),
        override val surface: CSSColorValue = Color.white,
        override val background: CSSColorValue = rgb(250, 250, 250),
        override val divider: CSSColorValue = rgba(0, 0, 0, 0.12),
        override val onPrimary: CSSColorValue = Color.white,
        override val onSecondary: CSSColorValue = Color.white,
        override val onSurface: CSSColorValue = Color.black,
        override val textPrimary: CSSColorValue = Color.black,
        override val textSecondary: CSSColorValue = Color.black,
        override val textLink: CSSColorValue = rgb(0, 181, 226),
    ) : Palette()

    data class Dark(
        override val primary: CSSColorValue = rgb(255, 204, 128),
        override val secondary: CSSColorValue = rgb(255, 240, 159),
        override val surface: CSSColorValue = rgb(48, 48, 48),
        override val background: CSSColorValue = rgb(18, 18, 18),
        override val divider: CSSColorValue = rgba(255, 255, 255, 0.12),
        override val onPrimary: CSSColorValue = Color.black,
        override val onSecondary: CSSColorValue = Color.black,
        override val onSurface: CSSColorValue = Color.white,
        override val textPrimary: CSSColorValue = Color.white,
        override val textSecondary: CSSColorValue = Color.white,
        override val textLink: CSSColorValue = rgb(0, 181, 226),
    ) : Palette()

    sealed class Alert {
        companion object {
            fun light(
                info: CSSColorValue = rgb(79, 195, 247),
                success: CSSColorValue = rgb(129, 199, 132),
                warning: CSSColorValue = rgb(255, 183, 77),
                error: CSSColorValue = rgb(229, 115, 115),
            ) = Light(info, success, warning, error)

            fun dark(
                info: CSSColorValue = rgb(41, 182, 246),
                success: CSSColorValue = rgb(102, 187, 106),
                warning: CSSColorValue = rgb(255, 167, 38),
                error: CSSColorValue = rgb(244, 67, 54),
            ) = Dark(info, success, warning, error)
        }

        abstract val info: CSSColorValue
        abstract val success: CSSColorValue
        abstract val warning: CSSColorValue
        abstract val error: CSSColorValue

        data class Light(
            override val info: CSSColorValue,
            override val success: CSSColorValue,
            override val warning: CSSColorValue,
            override val error: CSSColorValue,
        ) : Alert()

        data class Dark(
            override val info: CSSColorValue,
            override val success: CSSColorValue,
            override val warning: CSSColorValue,
            override val error: CSSColorValue,
        ) : Alert()
    }
}
