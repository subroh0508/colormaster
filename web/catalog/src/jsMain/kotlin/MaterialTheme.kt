import org.jetbrains.compose.web.css.*

class MaterialTheme(private val dark: Boolean = false) : StyleSheet() {
    companion object {
        const val primary = "#e65100"
        const val secondary = "#fff09f"
        const val surface = "#ffffff"
        const val background = "#fafafa"
    }

    object Var {
        val primary = Color("var(--${Name.primary}, ${MaterialTheme.primary})")
        val secondary = Color("var(--${Name.secondary}, ${MaterialTheme.secondary})")
        val surface = Color("var(--${Name.surface}, ${MaterialTheme.surface})")
        val background = Color("var(--${Name.background}, ${MaterialTheme.background})")
        val onPrimary = Color("var(--${Name.onPrimary}, #ffffff)")
        val onSecondary = Color("var(--${Name.onSecondary}, #ffffff)")
        val onSurface = Color("var(--${Name.onSurface}, rgba(0, 0, 0, 0.87))")
        val textPrimaryLight = Color("var(--${Name.textSecondaryLight}, rgba(0, 0, 0, 0.87))")
        val textPrimaryDark = Color("var(--${Name.textSecondaryDark}, #ffffff)")
        val textSecondaryLight = Color("var(--${Name.textSecondaryLight}, rgba(0, 0, 0, 0.60))")
        val textSecondaryDark = Color("var(--${Name.textSecondaryDark}, rgba(255, 255, 255, 0.70))")
    }

    private object Name {
        const val primary = "mdc-theme-primary"
        const val secondary = "mdc-theme-secondary"
        const val surface = "mdc-theme-surface"
        const val background = "mdc-theme-background"
        const val onPrimary = "mdc-theme-on-primary"
        const val onSecondary = "mdc-theme-on-secondary"
        const val onSurface = "mdc-theme-on-surface"
        const val textPrimaryLight = "mdc-theme-text-primary-on-light"
        const val textPrimaryDark = "mdc-theme-text-primary-on-dark"
        const val textSecondaryLight = "mdc-theme-text-secondary-on-light"
        const val textSecondaryDark = "mdc-theme-text-secondary-on-dark"
    }

    init {
        type(":root") style  {
            variable(Name.primary, Color(if (dark) "#ffcc80" else primary))
            variable("${Name.primary}-rgb", if (dark) "255,204,128" else "230,81,0")
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
            variable("mdc-checkbox-checked-color", Color(primary))
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