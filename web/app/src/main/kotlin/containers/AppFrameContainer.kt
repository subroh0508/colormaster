package containers

import KoinAppContext
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
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.w3c.dom.get
import org.w3c.dom.set
import react.*
import react.router.dom.useHistory
import themes.ThemeProvider
import utilities.Actions
import utilities.useTranslation
import kotlinx.browser.localStorage
import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.presentation.home.viewmodel.AuthenticationViewModel
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import org.koin.core.qualifier.named
import org.koin.dsl.module
import toSearchBy
import useQuery

@Suppress("FunctionName")
fun RBuilder.AppFrameContainer(handler: RHandler<RProps>) = child(AppContextProviderContainer, handler = handler)

private const val APP_FRAME_SCOPE_ID = "APP_FRAME_SCOPE"

private val AppPreferenceContext = createContext<AppPreference>()
private val AuthenticationContext = createContext<AuthenticationViewModel>()

private val AppContextProviderContainer = functionalComponent<RProps> { props ->
    val koinApp = useContext(KoinAppContext)
    val (appPreference, setAppPreference) = useState<AppPreference>()
    val (viewModel, setViewModel) = useState<AuthenticationViewModel>()

    useEffectOnce {
        val module = module {
            scope(named(APP_FRAME_SCOPE_ID)) {
                scoped { AuthenticationViewModel(get(), MainScope()) }
            }
        }

        koinApp.modules(module)
        koinApp.koin.createScope(APP_FRAME_SCOPE_ID, named(APP_FRAME_SCOPE_ID))

        setAppPreference(value = koinApp.koin.get())
        setViewModel(value = koinApp.koin.getScope(APP_FRAME_SCOPE_ID).get())
    }

    appPreference ?: return@functionalComponent
    viewModel ?: return@functionalComponent

    AppPreferenceContext.Provider {
        attrs.value = appPreference

        AuthenticationContext.Provider {
            attrs.value = viewModel

            child(AppFrameContainerComponent) {
                props.children()
            }
        }
    }
}

private val AppFrameContainerComponent = functionalComponent<RProps> { props ->
    val preferredType = if (useMediaQuery("(prefers-color-scheme: dark)")) PaletteType.dark else PaletteType.light
    val history = useHistory()
    val lang = language(history)
    val tab = SearchByTab.findByQuery(useQuery().get("by"))
    val (_, i18n) = useTranslation()

    val appPreference = useContext(AppPreferenceContext)

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
        appPreference.setLanguage(lang)
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
