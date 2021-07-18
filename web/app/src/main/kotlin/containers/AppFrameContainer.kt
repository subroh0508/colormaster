package containers

import koinApp
import components.atoms.MenuComponent
import components.atoms.appBarTop
import components.atoms.searchByTabs
import components.templates.appMenu
import isExpandAppBar
import isRoot
import kotlinext.js.jsObject
import language
import materialui.styles.palette.PaletteType
import materialui.useMediaQuery
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.w3c.dom.get
import org.w3c.dom.set
import react.*
import react.router.dom.useHistory
import themes.ThemeProvider
import utilities.Actions
import utilities.useTranslation
import kotlinx.browser.localStorage
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import toSearchBy
import useQuery

@Suppress("FunctionName")
fun RBuilder.AppFrameContainer(handler: RHandler<RProps>) = child(AppFrameContainerComponent, handler = handler)

private val AppFrameContainerComponent = functionComponent<RProps> { props ->
    val preferredType = if (useMediaQuery("(prefers-color-scheme: dark)")) PaletteType.dark else PaletteType.light
    val history = useHistory()
    val lang = language(history)
    val tab = SearchByTab.findByQuery(useQuery().get("by"))
    val (_, i18n) = useTranslation()

    val (appState, dispatch) = useReducer(
        reducer,
        AppState()
    )

    useEffectOnce {
        dispatch(actions(ActionType.CHANGE) {
            themeType = localStorage["paletteType"]?.let { PaletteType.valueOf(it) }
        })
    }
    useEffect(preferredType) {
        if (localStorage["paletteType"] != null) return@useEffect

        dispatch(actions(ActionType.CHANGE) {
            themeType = preferredType
        })
    }
    useEffect(lang.code == i18n.language) {
        AppPreferenceController.changeLanguage(lang)
        i18n.changeLanguage(lang.code)
    }
    useEffect(appState) { localStorage["paletteType"] = appState.themeType.name }

    fun closeMenu() = dispatch(actions(ActionType.CHANGE) { openDrawer = false })
    fun toggleMenu() = dispatch(actions(ActionType.CHANGE) { openDrawer = !appState.openDrawer })
    fun toggleTheme() = dispatch(actions(ActionType.CHANGE) { themeType = if (appState.themeType == PaletteType.light) PaletteType.dark else PaletteType.light })

    ThemeProvider {
        attrs.paletteType = appState.themeType

        appBarTop {
            attrs.themeType = appState.themeType
            attrs.lang = lang
            attrs.pathname = history.location.pathname
            attrs.openDrawer = appState.openDrawer
            attrs.expand = isExpandAppBar(history)
            attrs.MenuComponent {
                appMenu { attrs.onCloseMenu = { closeMenu() } }
            }
            attrs.onClickChangeTheme = { toggleTheme() }
            attrs.onClickMenuIcon = { toggleMenu() }
            attrs.onCloseMenu = { closeMenu() }

            if (isRoot(history)) {
                searchByTabs {
                    attrs.index = tab.ordinal
                    attrs.onChangeTab = history::toSearchBy
                }
            }
        }

        props.children()
    }
}

private data class AppState(
    val themeType: PaletteType = PaletteType.light,
    val openDrawer: Boolean = false
)

private enum class ActionType {
    CHANGE
}

private external interface Payload {
    var themeType: PaletteType?
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
            openDrawer = payload.openDrawer ?: state.openDrawer
        )
    }
}

private object AppPreferenceController : KoinComponent {
    private val browserPref: AppPreference by inject()

    fun changeLanguage(lang: Languages) { browserPref.setLanguage(lang) }

    override fun getKoin() = koinApp.koin
}
