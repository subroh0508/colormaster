package components.templates

import components.atoms.MenuComponent
import components.atoms.appBarTop
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

val APP_BAR_SM_UP = 408.px

fun RBuilder.appFrame(handler: RHandler<RProps>) = child(AppFrameComponent, handler = handler)

private val AppFrameComponent = functionalComponent<RProps> { props ->
    val preferredType = if (useMediaQuery("(prefers-color-scheme: dark)")) PaletteType.dark else PaletteType.light
    val (appState, dispatch) = useReducer(reducer, AppState())
    val history = useHistory()

    useEffect(listOf()) {
        dispatch(actions(ActionType.CHANGE) {
            themeType = localStorage["paletteType"]?.let { PaletteType.valueOf(it) }
            lang = localStorage["lang"]
        })
    }
    useEffect(listOf(preferredType)) {
        if (localStorage["paletteType"] != null) return@useEffect

        dispatch(actions(ActionType.CHANGE) { themeType = preferredType })
    }
    useEffect(listOf(appState)) {
        localStorage["paletteType"] = appState.themeType.name
        localStorage["lang"] = appState.lang
    }

    ThemeProvider {
        attrs.paletteType = appState.themeType

        appBarTop {
            attrs.themeType = appState.themeType
            attrs.openDrawer = appState.openDrawer
            attrs.expand = isExpandAppBar(history)
            attrs.MenuComponent {
                appMenu {
                    attrs.onCloseMenu = { dispatch(actions(ActionType.CHANGE) { openDrawer = false }) }
                }
            }
            attrs.onClickChangeTheme = {
                dispatch(actions(ActionType.CHANGE) { themeType = if (appState.themeType == PaletteType.light) PaletteType.dark else PaletteType.light })
            }
            attrs.onClickMenuIcon = { dispatch(actions(ActionType.CHANGE) { openDrawer = !appState.openDrawer }) }
            attrs.onCloseMenu = { dispatch(actions(ActionType.CHANGE) { openDrawer = false }) }
        }

        props.children()
    }
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
