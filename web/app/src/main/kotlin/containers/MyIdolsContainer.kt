package containers

import AuthenticationProviderContext
import KoinComponent
import KoinContext
import components.templates.myIdolsCards
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.presentation.myidols.model.MyIdolsUiModel
import net.subroh0508.colormaster.presentation.myidols.viewmodel.MyIdolsViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pages.MyIdolsPage
import react.*

@Suppress("FunctionName")
fun RBuilder.MyIdolsContainer() = child(MyIdolsContextProvider) { child(MyIdolsContainer) }

private const val MY_IDOLS_SCOPE_ID = "MY_IDOLS_SCOPE"

val MyIdolsDispatcherContext = createContext<MyIdolsViewModel>()
val MyIdolsProviderContext = createContext<MyIdolsUiModel>()

private fun module(appScope: CoroutineScope) = module {
    scope(named(MY_IDOLS_SCOPE_ID)) {
        scoped { MyIdolsViewModel(get(), appScope) }
    }
}

private val MyIdolsContextProvider = KoinComponent(MyIdolsDispatcherContext, MY_IDOLS_SCOPE_ID, ::module)

private val MyIdolsContainer = functionalComponent<RProps> {
    val (_, appScope) = useContext(KoinContext)
    val currentUser = useContext(AuthenticationProviderContext)
    val viewModel = useContext(MyIdolsDispatcherContext)

    val (uiModel, setUiModel) = useState(MyIdolsUiModel())

    useEffectOnce {
        viewModel.uiModel.onEach {
            setUiModel(it)
        }.launchIn(appScope)
    }

    useEffect(currentUser) {
        if (currentUser == null) return@useEffect

        viewModel.loadFavorites()
    }

    MyIdolsProviderContext.Provider {
        attrs.value = uiModel

        MyIdolsPage()
    }
}
