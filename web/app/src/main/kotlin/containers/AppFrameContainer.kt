package containers

import AuthenticationDispatcherContext
import AuthenticationProviderContext
import KoinContext
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.home.viewmodel.JsAuthenticationViewModel
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import org.koin.core.qualifier.named
import org.koin.dsl.module
import toSearchBy
import useQuery
import utilities.BrowserAppPreference

@Suppress("FunctionName")
fun RBuilder.AppFrameContainer(handler: RHandler<RProps>) = child(AppContextProvider, handler = handler)

private const val APP_FRAME_SCOPE_ID = "APP_FRAME_SCOPE"

private val AppPreferenceDispatcherContext = createContext<AppPreference>()

private val AppContextProvider = functionalComponent<RProps> { props ->
    val (koinApp, appScope) = useContext(KoinContext)

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

    AppPreferenceDispatcherContext.Provider {
        attrs.value = appPreference

        AuthenticationDispatcherContext.Provider {
            attrs.value = viewModel

            child(AppFrameContainer) {
                props.children()
            }
        }
    }
}

private val AppFrameContainer = functionalComponent<RProps> { props ->
    val preferredType = if (useMediaQuery("(prefers-color-scheme: dark)")) PaletteType.dark else PaletteType.light
    val history = useHistory()
    val lang = language(history)

    val appPreference = useContext(AppPreferenceDispatcherContext)

    val (appPreferenceState, setAppPreferenceState) = useState(BrowserAppPreference.State(appPreference))

    useEffectOnce {
        val themeType = appPreference.themeType ?: return@useEffectOnce

        setAppPreferenceState(appPreferenceState.copy(themeType = themeType))
    }

    useEffect(preferredType) {
        if (appPreference.themeType != null) return@useEffect

        setAppPreferenceState(appPreferenceState.copy(themeType = preferredType))
    }
    useEffect(lang.code == appPreference.lang.code) { appPreference.setLanguage(lang) }

    useEffect(appPreferenceState) { appPreference.setThemeType(appPreferenceState.themeType) }

    fun toggleTheme() {
        setAppPreferenceState(appPreferenceState.copy(
            themeType = if (appPreferenceState.themeType == PaletteType.light)
                PaletteType.dark
            else
                PaletteType.light
        ))
    }

    val tab = SearchByTab.findByQuery(useQuery().get("by"))

    val (_, appScope) = useContext(KoinContext)
    val viewModel = useContext(AuthenticationDispatcherContext)

    val (openDrawer, setOpenDrawer) = useState(false)
    val (currentUser, setCurrentUser) = useState<CurrentUser?>(null)

    useEffectOnce {
        viewModel.uiModel.onEach {
            setCurrentUser(it.currentUser)
        }.launchIn(appScope)

        viewModel.subscribe()
    }

    fun closeMenu() { setOpenDrawer(false) }
    fun toggleMenu() { setOpenDrawer(!openDrawer) }

    ThemeProvider {
        attrs.paletteType = appPreferenceState.themeType

        AuthenticationProviderContext.Provider {
            attrs.value = currentUser

            appBarTop {
                attrs.themeType = appPreferenceState.themeType
                attrs.lang = appPreferenceState.lang
                attrs.pathname = history.location.pathname
                attrs.openDrawer = openDrawer
                attrs.expand = isExpandAppBar(history)
                attrs.MenuComponent {
                    appMenu {
                        attrs.currentUser = currentUser
                        attrs.onCloseMenu = ::closeMenu
                    }
                }
                attrs.onClickChangeTheme = { toggleTheme() }
                attrs.onClickMenuIcon = { toggleMenu() }
                attrs.onCloseMenu = ::closeMenu

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
}
