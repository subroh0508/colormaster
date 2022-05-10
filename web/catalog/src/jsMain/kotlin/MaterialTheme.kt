import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.rgba
import org.jetbrains.compose.web.css.selectors.type

class MaterialTheme(private val dark: Boolean = false) : StyleSheet() {
    companion object {
        const val primary = "#e65100"
        const val secondary = "#fff09f"
        const val surface = "#ffffff"
        const val background = "#fafafa"
    }

    object Var {
        const val primary = "var(--${Name.primary}, ${MaterialTheme.primary})"
        const val secondary = "var(--${Name.secondary}, ${MaterialTheme.secondary})"
        const val surface = "var(--${Name.surface}, ${MaterialTheme.surface})"
        const val background = "var(--${Name.background}, ${MaterialTheme.background})"
        const val onPrimary = "var(--${Name.onPrimary}, #ffffff)"
        const val onSecondary = "var(--${Name.onSecondary}, #ffffff)"
        const val onSurface = "var(--${Name.onSurface}, rgba(0, 0, 0, 0.87))"
        const val textPrimaryLight = "var(--${Name.textSecondaryLight}, rgba(0, 0, 0, 0.87))"
        const val textPrimaryDark = "var(--${Name.textSecondaryDark}, #ffffff)"
        const val textSecondaryLight = "var(--${Name.textSecondaryLight}, rgba(0, 0, 0, 0.60))"
        const val textSecondaryDark = "var(--${Name.textSecondaryDark}, rgba(255, 255, 255, 0.70))"
    }

    private object Name {
        const val primary = "mdc-theme-primary"
        const val secondary = "mdc-theme-secondary"
        const val surface = "mdc-theme-surface"
        const val background = "mdc-theme-background"
        const val onPrimary = "mdc-theme-on-primary"
        const val onSecondary = "mdc-theme-on-secondary"
        const val onSurface = "mdc-theme-on-surface"
        const val textPrimaryLight = "--mdc-theme-text-primary-on-light"
        const val textPrimaryDark = "--mdc-theme-text-primary-on-dark"
        const val textSecondaryLight = "--mdc-theme-text-secondary-on-light"
        const val textSecondaryDark = "--mdc-theme-text-secondary-on-dark"
    }

    init {
        type(":root") style  {
            variable(Name.primary, Color(if (dark) "#ffcc80" else primary))
            variable(Name.secondary, Color(secondary))
            variable(Name.surface, Color(if (dark) "#303030" else surface))
            variable(Name.background, Color(if (dark) "#121212" else background))
            variable(Name.onPrimary, if (dark) rgba(0, 0, 0, 0.87) else Color.white)
            variable(Name.onSecondary, if (dark) rgba(0, 0, 0, 0.87) else Color.white)
            variable(Name.onSurface, if (dark) Color.white else rgba(0, 0, 0, 0.87))
            variable(Name.textPrimaryLight, rgba(0, 0, 0, 0.87))
            variable(Name.textPrimaryDark, Color.white)
            variable(Name.textSecondaryLight, rgba(0, 0, 0, 0.60))
            variable(Name.textSecondaryDark, rgba(255, 255, 255, 0.70))
        }
    }
}