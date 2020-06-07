package containers

import Languages
import components.atoms.MenuComponent
import components.atoms.appBarTop
import components.templates.appMenu
import isExpandAppBar
import kotlinext.js.jsObject
import kotlinx.css.*
import materialui.styles.palette.PaletteType
import materialui.useMediaQuery
import org.w3c.dom.get
import org.w3c.dom.set
import react.*
import react.router.dom.useHistory
import themes.ThemeProvider
import utilities.Actions
import kotlin.browser.localStorage

@Suppress("FunctionName")
fun RBuilder.AppFrameContainer(handler: RHandler<AppFrameContainerProps>) = child(AppFrameContainerComponent, handler = handler)

private val AppFrameContainerComponent = functionalComponent<AppFrameContainerProps> { props ->
    val preferredType = if (useMediaQuery("(prefers-color-scheme: dark)")) PaletteType.dark else PaletteType.light
    val (appState, dispatch) = useReducer(
        reducer,
        AppState()
    )
    val history = useHistory()

    useEffect(listOf()) {
        dispatch(actions(ActionType.CHANGE) {
            themeType = localStorage["paletteType"]?.let { PaletteType.valueOf(it) }
            //lang = localStorage["lang"]
        })
    }
    useEffect(listOf(preferredType)) {
        if (localStorage["paletteType"] != null) return@useEffect

        dispatch(actions(ActionType.CHANGE) {
            themeType = preferredType
        })
    }
    useEffect(listOf(appState)) {
        localStorage["paletteType"] = appState.themeType.name
        //localStorage["lang"] = appState.lang
    }

    fun closeMenu() = dispatch(actions(ActionType.CHANGE) { openDrawer = false })
    fun toggleMenu() = dispatch(actions(ActionType.CHANGE) { openDrawer = !appState.openDrawer })
    fun toggleTheme() = dispatch(actions(ActionType.CHANGE) { themeType = if (appState.themeType == PaletteType.light) PaletteType.dark else PaletteType.light })

    ThemeProvider {
        attrs.paletteType = appState.themeType

        appBarTop {
            attrs.themeType = appState.themeType
            attrs.lang = props.lang
            attrs.pathname = history.location.pathname
            attrs.openDrawer = appState.openDrawer
            attrs.expand = isExpandAppBar(history)
            attrs.MenuComponent {
                appMenu { attrs.onCloseMenu = { closeMenu() } }
            }
            attrs.onClickChangeTheme = { toggleTheme() }
            attrs.onClickMenuIcon = { toggleMenu() }
            attrs.onCloseMenu = { closeMenu() }
        }

        props.children()
    }
}

external interface AppFrameContainerProps : RProps {
    var lang: Languages
}

private data class AppState(
    val themeType: PaletteType = PaletteType.light,
    val lang: String = "ja",
    val openDrawer: Boolean = false
)

private enum class ActionType {
    CHANGE
}

private external interface Payload {
    var themeType: PaletteType?
    var lang: String?
    var openDrawer: Boolean?
}

private fun actions(
    type: ActionType,
    payload: Payload.() -> Unit
) = utilities.actions<ActionType, Payload> {
    this.type = type
    this.payload = jsObject(payload)
}

private val reducer = { state: AppState, action: Actions<ActionType, Payload> ->
    val payload = action.payload

    when (action.type) {
        ActionType.CHANGE -> AppState(
            themeType = payload.themeType ?: state.themeType,
            lang = payload.lang ?: state.lang,
            openDrawer = payload.openDrawer ?: state.openDrawer
        )
    }
}
