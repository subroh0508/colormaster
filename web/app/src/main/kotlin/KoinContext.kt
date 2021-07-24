import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.components.core.AppModule
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.home.viewmodel.JsAuthenticationViewModel
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import react.*
import utilities.AppPreferenceModule
import utilities.useTranslation

val KoinContext = createContext<Pair<KoinApplication, CoroutineScope>>()

val AuthenticationProviderContext = createContext<CurrentUser?>()
val AuthenticationDispatcherContext = createContext<JsAuthenticationViewModel>()

@Suppress("FunctionName")
fun RBuilder.KoinAppProvider(
    scope: CoroutineScope = MainScope(),
    handler: RHandler<RProps>,
) = child(functionalComponent { props ->
    val (_, i18n) = useTranslation()

    val koinApp = koinApplication {
        modules(AppModule + AppPreferenceModule(i18n))
    }

    KoinContext.Provider {
        attrs.value = koinApp to scope

        props.children()
    }
}, handler = handler)
