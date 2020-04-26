package components.templates

import components.atoms.MenuComponent
import components.atoms.appBarTop
import isExpandAppBar
import kotlinx.css.*
import materialui.styles.palette.PaletteType
import materialui.useMediaQuery
import react.*
import react.router.dom.useHistory
import themes.ThemeProvider

val APP_BAR_SM_UP = 408.px

fun RBuilder.appFrame(handler: RHandler<RProps>) = child(AppFrameComponent, handler = handler)

private val AppFrameComponent = functionalComponent<RProps> { props ->
    val prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)")
    val (appState, setAppState) = useState(AppState(
        themeType = PaletteType.light,
        lang = "ja",
        openDrawer = false
    ))
    val history = useHistory()

    useEffect(listOf(prefersDarkMode)) {
        setAppState(appState.copy(themeType = if (prefersDarkMode) PaletteType.dark else PaletteType.light))
    }

    ThemeProvider {
        attrs.paletteType = appState.themeType

        appBarTop {
            attrs.themeType = appState.themeType
            attrs.openDrawer = appState.openDrawer
            attrs.expand = isExpandAppBar(history)
            attrs.MenuComponent {
                appMenu {
                    attrs.onCloseMenu = { setAppState(appState.copy(openDrawer = false)) }
                }
            }
            attrs.onClickChangeTheme = { setAppState(
                appState.copy(themeType = if (appState.themeType == PaletteType.light) PaletteType.dark else PaletteType.light))
            }
            attrs.onClickMenuIcon = { setAppState(appState.copy(openDrawer = !appState.openDrawer)) }
            attrs.onCloseMenu = { setAppState(appState.copy(openDrawer = false)) }
        }

        props.children()
    }
}

private data class AppState(
    val themeType: PaletteType,
    val lang: String,
    val openDrawer: Boolean
)
