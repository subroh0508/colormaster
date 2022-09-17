import net.subroh0508.colormaster.model.HexColor
import org.jetbrains.compose.web.css.*
import utilities.alpha
import utilities.darken
import utilities.lighten

class MaterialTheme(private val palette: Palette) : StyleSheet() {
    constructor(dark: Boolean = false) : this(if (dark) Palette.Dark() else Palette.Light())

    private val dark = palette is Palette.Dark

    object Var {
        val primary = Color("var(--${Name.primary})")
        val secondary = Color("var(--${Name.secondary})")
        val surface = Color("var(--${Name.surface})")
        val background = Color("var(--${Name.background})")
        val divider = Color("var(--${Name.divider})")
        val onPrimary = Color("var(--${Name.onPrimary})")
        val onSecondary = Color("var(--${Name.onSecondary})")
        val onSurface = Color("var(--${Name.onSurface})")
        val textLink = Color("var(--${Name.textLink})")

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
        val textHint = Color("var(--${Name.textHint})")
        val textDisabled = Color("var(--${Name.textDisabled})")

        val ripple = Color("var(--${Name.ripple})")
    }

    private object Name {
        const val primary = "mdc-theme-primary"
        const val secondary = "mdc-theme-secondary"
        const val surface = "mdc-theme-surface"
        const val background = "mdc-theme-background"
        const val divider = "mdc-theme-divider"
        const val onPrimary = "mdc-theme-on-primary"
        const val onSecondary = "mdc-theme-on-secondary"
        const val onSurface = "mdc-theme-on-surface"
        const val textLink = "mdc-theme-text-link"
        const val textPrimary = "mdc-theme-text-primary"
        const val textSecondary = "mdc-theme-text-secondary"
        const val textHint = "mdc-theme-text-hint"
        const val textDisabled = "mdc-theme-text-disabled"

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

        const val checkboxChecked = "mdc-checkbox-checked-color"
        const val ripple = "mdc-ripple-color"
        const val chipSurface = "mdc-chip-surface-color"
        const val chipSurfaceActivated = "mdc-chip-surface-activated-color"
        const val textFieldDisabledOutline = "mdc-textfield-disabled-outline-color"
    }

    init {
        val lightAlert = Palette.Alert.light()
        val darkAlert = Palette.Alert.dark()

        val alert = if (dark) darkAlert else lightAlert
        fun alertBackground(color: CSSColorValue) = if (dark) color.darken(0.9) else color.lighten(0.9)
        fun alertText(color: CSSColorValue) = if (dark) color.lighten(0.6) else color.darken(0.6)

        type(":root") style  {
            variable(Name.primary, palette.primary)
            variable(Name.secondary, palette.secondary)
            variable(Name.surface, palette.surface)
            variable(Name.background, palette.background)
            variable(Name.divider, palette.divider)
            variable(Name.onPrimary, palette.onPrimary.alpha(if (dark) 0.87 else 1))
            variable(Name.onSecondary, palette.onSecondary.alpha(if (dark) 0.87 else 1))
            variable(Name.onSurface, palette.onSurface.alpha(if (dark) 1 else 0.87))
            variable(Name.textPrimary, palette.textPrimary.alpha(if (dark) 1 else 0.87))
            variable(Name.textSecondary, palette.textSecondary.alpha(if (dark) 0.70 else 0.60))
            variable(Name.textHint, palette.textPrimary.alpha(if (dark) 0.38 else 0.50))
            variable(Name.textDisabled, palette.textPrimary.alpha(if (dark) 0.38 else 0.50))
            variable(Name.textLink, palette.textLink)

            variable(Name.info, alert.info)
            variable(Name.success, alert.success)
            variable(Name.warning, alert.warning)
            variable(Name.error, alert.error)
            variable(Name.backgroundInfo, alertBackground(lightAlert.info))
            variable(Name.backgroundSuccess, alertBackground(lightAlert.success))
            variable(Name.backgroundWarning, alertBackground(lightAlert.warning))
            variable(Name.backgroundError, alertBackground(lightAlert.error))
            variable(Name.textInfo, alertText(lightAlert.info))
            variable(Name.textSuccess, alertText(lightAlert.success))
            variable(Name.textWarning, alertText(lightAlert.warning))
            variable(Name.textError, alertText(lightAlert.error))

            variable(Name.checkboxChecked, palette.primary)
            variable(Name.ripple, if (dark) Color.white else Color.black)
            variable(Name.chipSurface, (if (dark) Color.white else Color.black).alpha(0.12))
            variable(Name.chipSurfaceActivated, palette.primary.alpha(0.12))
            variable(Name.textFieldDisabledOutline, palette.textPrimary.alpha(0.06))
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