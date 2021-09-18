@file:Suppress("FunctionName")

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.components.core.AppModule
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.common.ViewModel
import net.subroh0508.colormaster.presentation.home.viewmodel.JsAuthenticationViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import react.*
import utilities.AppPreferenceModule
import utilities.useTranslation

val KoinContext = createContext<Pair<KoinApplication, CoroutineScope>>()

val AuthenticationProviderContext = createContext<CurrentUser?>()
val AuthenticationDispatcherContext = createContext<JsAuthenticationViewModel>()

fun RBuilder.KoinAppProvider(
    scope: CoroutineScope = MainScope(),
    handler: RHandler<PropsWithChildren>,
) = child(functionComponent { props ->
    val (_, i18n) = useTranslation()

    val koinApp = koinApplication {
        modules(AppModule + AppPreferenceModule(i18n))
    }

    KoinContext.Provider {
        attrs.value = koinApp to scope

        props.children()
    }
}, handler = handler)

inline fun <reified VM: ViewModel> KoinComponent(
    context: Context<VM>,
    scopeId: String,
    crossinline module: (CoroutineScope) -> Module,
    crossinline handler: RBuilder.(PropsWithChildren) -> Unit = { it.children() },
) = KoinComponent<VM, PropsWithChildren>(context, scopeId, module, handler)

inline fun <reified VM: ViewModel, P: Props> KoinComponent(
    context: Context<VM>,
    scopeId: String,
    crossinline module: (CoroutineScope) -> Module,
    crossinline handler: RBuilder.(P) -> Unit,
) = functionComponent<P> { props ->
    val (koinApp, appScope) = useContext(KoinContext)
    val (viewModel, setViewModel) = useState<VM>()

    useEffectOnce {
        val m = module(appScope)

        koinApp.modules(m)
        koinApp.koin.createScope(scopeId, named(scopeId))

        setViewModel(value = koinApp.koin.getScope(scopeId).get())

        cleanup {
            koinApp.unloadModules(m)
            koinApp.koin.deleteScope(scopeId)
        }
    }

    viewModel ?: return@functionComponent

    context.Provider {
        attrs.value = viewModel

        handler(props)
    }
}
