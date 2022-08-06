import net.subroh0508.colormaster.model.HexColor
import org.jetbrains.compose.web.css.*
import utilities.darken
import utilities.lighten

class MaterialTheme(private val palette: Palette) : StyleSheet() {
    constructor(dark: Boolean = false) : this(if (dark) Palette.Dark() else Palette.Light())

    private val dark = palette is Palette.Dark

    companion object {
        const val primary = "#e65100"
        const val secondary = "#fff09f"
        const val surface = "#ffffff"
        const val background = "#fafafa"
        const val textLink = "#00B5E2"

        const val info = "#4FC3F7"
        const val success = ""
        const val warning = ""
        const val error = ""
        const val textInfo = "#063D6B"
        const val textSuccess = ""
        const val textWarning = ""
        const val textError = ""
    }

    object Var {
        val primary = Color("var(--${Name.primary}, ${MaterialTheme.primary})")
        val secondary = Color("var(--${Name.secondary}, ${MaterialTheme.secondary})")
        val surface = Color("var(--${Name.surface}, ${MaterialTheme.surface})")
        val background = Color("var(--${Name.background}, ${MaterialTheme.background})")
        val onPrimary = Color("var(--${Name.onPrimary}, #ffffff)")
        val onSecondary = Color("var(--${Name.onSecondary}, #ffffff)")
        val onSurface = Color("var(--${Name.onSurface}, rgba(0, 0, 0, 0.87))")
        val textLink = Color("var(--${Name.textLink}, ${MaterialTheme.textLink})")
        val textPrimaryLight = Color("var(--${Name.textPrimaryLight}, rgba(0, 0, 0, 0.87))")
        val textPrimaryDark = Color("var(--${Name.textPrimaryDark}, #ffffff)")
        val textSecondaryLight = Color("var(--${Name.textSecondaryLight}, rgba(0, 0, 0, 0.60))")
        val textSecondaryDark = Color("var(--${Name.textSecondaryDark}, rgba(255, 255, 255, 0.70))")

        val info = Color("var(--${Name.info})")
        val success = Color("var(--${Name.success})")
        val warning = Color("var(--${Name.warning})")
        val error = Color("var(--${Name.error})")
        val backgroundInfo = Color("var(--${Name.backgroundInfo})")
        val backgroundSuccess = Color("var(--${Name.backgroundSuccess})")
        val backgroundWarning = Color("var(--${Name.backgroundWarning})")
        val backgroundError = Color("var(--${Name.backgroundError})")
        val textInfo = Color("var(--${Name.textInfo})")
        val textSuccess = Color("var(--${Name.textSuccess})")
        val textWarning = Color("var(--${Name.textWarning})")
        val textError = Color("var(--${Name.textError})")

        val ripple = Color("var(--${Name.ripple}, #000000)")
    }

    private object Name {
        const val primary = "mdc-theme-primary"
        const val secondary = "mdc-theme-secondary"
        const val surface = "mdc-theme-surface"
        const val background = "mdc-theme-background"
        const val onPrimary = "mdc-theme-on-primary"
        const val onSecondary = "mdc-theme-on-secondary"
        const val onSurface = "mdc-theme-on-surface"
        const val textLink = "mdc-theme-text-link"
        const val textPrimary = "mdc-theme-text-primary"
        const val textPrimaryLight = "mdc-theme-text-primary-on-light"
        const val textPrimaryDark = "mdc-theme-text-primary-on-dark"
        const val textSecondary = "mdc-theme-text-secondary"
        const val textSecondaryLight = "mdc-theme-text-secondary-on-light"
        const val textSecondaryDark = "mdc-theme-text-secondary-on-dark"
        const val ripple = "mdc-ripple-color"

        const val info = "mdc-theme-info"
        const val success = "mdc-theme-success"
        const val warning = "mdc-theme-warning"
        const val error = "mdc-theme-error"
        const val backgroundInfo = "mdc-theme-background-info"
        const val backgroundSuccess = "mdc-theme-background-success"
        const val backgroundWarning = "mdc-theme-background-warning"
        const val backgroundError = "mdc-theme-background-error"
        const val textInfo = "mdc-theme-text-info"
        const val textSuccess = "mdc-theme-text-success"
        const val textWarning = "mdc-theme-text-warning"
        const val textError = "mdc-theme-text-error"
    }

    init {
        val lightAlert = Palette.Alert.light()
        val darkAlert = Palette.Alert.dark()

        val alert = if (dark) darkAlert else lightAlert
        fun alertBackground(color: CSSColorValue) = if (dark) color.darken(0.9) else color.lighten(0.9)
        fun alertText(color: CSSColorValue) = if (dark) color.lighten(0.6) else color.darken(0.6)

        type(":root") style  {
            variable(Name.primary, palette.primary())
            variable("${Name.primary}-rgb", if (dark) "255,204,128" else "230,81,0")
            variable(Name.secondary, palette.secondary())
            variable(Name.surface, palette.surface())
            variable(Name.background, palette.background())
            variable(Name.onPrimary, palette.onPrimary(if (dark) 0.87 else 1))
            variable(Name.onSecondary, palette.onSecondary(if (dark) 0.87 else 1))
            variable(Name.onSurface, palette.onSurface(if (dark) 1 else 0.87))
            variable(Name.textPrimary, palette.textPrimary(if (dark) 1 else 0.87))
            variable(Name.textSecondary, palette.textSecondary(if (dark) 0.70 else 0.60))
            variable(Name.textLink, palette.textLink())

            variable(Name.info, alert.info())
            variable(Name.success, alert.success())
            variable(Name.warning, alert.warning())
            variable(Name.error, alert.error())
            variable(Name.backgroundInfo, alertBackground(lightAlert.info()))
            variable(Name.backgroundSuccess, alertBackground(lightAlert.success()))
            variable(Name.backgroundWarning, alertBackground(lightAlert.warning()))
            variable(Name.backgroundError, alertBackground(lightAlert.error()))
            variable(Name.textInfo, alertText(lightAlert.info()))
            variable(Name.textSuccess, alertText(lightAlert.success()))
            variable(Name.textWarning, alertText(lightAlert.warning()))
            variable(Name.textError, alertText(lightAlert.error()))

            variable("mdc-checkbox-checked-color", Color(primary))
            variable(Name.ripple, if (dark) Color.white else Color.black)
        }

        type("html") style  {
            height(100.percent)
            margin(0.px)
        }
        type("body") style {
            height(100.percent)
            margin(0.px)
            backgroundColor(Var.background)
        }
        type("style") style  {
            margin(0.px)
        }
    }
}