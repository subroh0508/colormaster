package themes

import kotlinx.css.Color
import materialui.components.cssbaseline.cssBaseline
import materialui.styles.createMuiTheme
import materialui.styles.muitheme.options.palette
import materialui.styles.palette.PaletteType
import materialui.styles.palette.options.*
import materialui.styles.themeprovider.themeProvider
import react.*

@Suppress("FunctionName")
fun RBuilder.ThemeProvider(handler: RHandler<ThemeProviderProps>) = child(ThemeProviderComponent, handler = handler)

private val ThemeProviderComponent = functionalComponent<ThemeProviderProps> { props ->
    val theme = useMemo({ createTheme(props.paletteType) }, arrayOf(props.paletteType))

    themeProvider(theme) {
        cssBaseline { }
        props.children()
    }
}

external interface ThemeProviderProps : RProps {
    var paletteType: PaletteType
}

private fun createTheme(paletteType: PaletteType) = createMuiTheme {
    palette {
        type = paletteType

        when (paletteType) {
            PaletteType.light -> {
                primary {
                    main = Color("#e65100")
                }
                secondary {
                    main = Color("#fff09f")
                }
                background {
                    paper = Color.white
                    default = Color("#fafafa")
                }
            }
            PaletteType.dark -> {
                primary {
                    main = Color("#ffcc80")
                }
                secondary {
                    main = Color("#fff09f")
                }
                background {
                    paper = Color("#303030")
                    default = Color("#121212")
                }
            }
        }
    }
}
