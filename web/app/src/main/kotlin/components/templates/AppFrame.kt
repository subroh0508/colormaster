package components.templates

import components.atoms.MenuComponent
import components.atoms.appBarTop
import isExpandAppBar
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.css.properties.BoxShadows
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarColor
import materialui.components.appbar.enums.AppBarPosition
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.palette.PaletteType
import materialui.styles.palette.paper
import react.*
import react.dom.div
import react.router.dom.useHistory
import themes.ThemeProvider

val APP_BAR_SM_UP = 408.px

fun RBuilder.appFrame(handler: RHandler<RProps>) = child(AppFrameComponent, handler = handler)

private val AppFrameComponent = functionalComponent<RProps> { props ->
    val (appState, setAppState) = useState(AppState(
        themeType = PaletteType.light,
        lang = "ja",
        openDrawer = false
    ))
    val history = useHistory()

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
