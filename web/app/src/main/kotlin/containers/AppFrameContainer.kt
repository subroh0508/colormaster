package containers

import KoinAppContext
import components.atoms.MenuComponent
import components.atoms.appBarTop
import components.atoms.searchByTabs
import components.templates.appMenu
import isExpandAppBar
import isRoot
import language
import materialui.styles.palette.PaletteType
import materialui.useMediaQuery
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import react.*
import react.router.dom.useHistory
import themes.ThemeProvider
import utilities.useTranslation
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.model.ui.commons.ThemeType
import net.subroh0508.colormaster.presentation.home.viewmodel.JsAuthenticationViewModel
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import org.koin.core.qualifier.named
import org.koin.dsl.module
import toSearchBy
import useQuery

@Suppress("FunctionName")
fun RBuilder.AppFrameContainer(handler: RHandler<RProps>) = child(AppContextProviderContainer, handler = handler)

private const val APP_FRAME_SCOPE_ID = "APP_FRAME_SCOPE"

private val AppPreferenceContext = createContext<AppPreference>()
val AuthenticationContext = createContext<JsAuthenticationViewModel>()

private val AppContextProviderContainer = functionalComponent<RProps> { props ->
    val (koinApp, appScope) = useContext(KoinAppContext)

    val (appPreference, setAppPreference) = useState<AppPreference>()
    val (viewModel, setViewModel) = useState<JsAuthenticationViewModel>()

    useEffectOnce {
        val module = module {
            scope(named(APP_FRAME_SCOPE_ID)) {
                scoped { JsAuthenticationViewModel(get(), appScope) }
            }
        }

        koinApp.modules(module)
        koinApp.koin.createScope(APP_FRAME_SCOPE_ID, named(APP_FRAME_SCOPE_ID))

        setAppPreference(value = koinApp.koin.get())
        setViewModel(value = koinApp.koin.getScope(APP_FRAME_SCOPE_ID).get())

        cleanup {
            koinApp.unloadModules(module)
            koinApp.koin.deleteScope(APP_FRAME_SCOPE_ID)
        }
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

    val (_, appScope) = useContext(KoinAppContext)
    val appPreference = useContext(AppPreferenceContext)
    val viewModel = useContext(AuthenticationContext)

    val (appState, setAppState) = useState(AppState(themeType = appPreference.themeType ?: ThemeType.light))

    useEffectOnce {
        appPreference.themeType?.let { setAppState(appState.copy(themeType = it)) }
    }
    useEffectOnce {
        viewModel.uiModel.onEach {
            setAppState(appState.copy(currentUser = it.currentUser))
        }.launchIn(appScope)

        viewModel.fetchCurrentUser()
    }
    useEffect(preferredType) {
        if (appPreference.themeType != null) return@useEffect

        setAppState(appState.copy(themeType = preferredType))
    }
    useEffect(lang.code == i18n.language) {
        appPreference.setLanguage(lang)
        i18n.changeLanguage(lang.code)
    }
    useEffect(appState) { appPreference.setThemeType(appState.themeType) }

    fun closeMenu() { setAppState(appState.copy(openDrawer = false)) }
    fun toggleMenu() { setAppState(appState.copy(openDrawer = !appState.openDrawer)) }
    fun toggleTheme() { setAppState(appState.copy(themeType = if (appState.themeType == PaletteType.light) PaletteType.dark else PaletteType.light)) }

    ThemeProvider {
        attrs.paletteType = appState.themeType

        appBarTop {
            attrs.themeType = appState.themeType
            attrs.lang = lang
            attrs.pathname = history.location.pathname
            attrs.openDrawer = appState.openDrawer
            attrs.expand = isExpandAppBar(history)
            attrs.MenuComponent {
                appMenu {
                    attrs.currentUser = appState.currentUser
                    attrs.onCloseMenu = { closeMenu() }
                }
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
    val openDrawer: Boolean = false,
    val currentUser: CurrentUser? = null,
)
