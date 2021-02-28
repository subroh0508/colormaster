import kotlinext.js.getOwnPropertyNames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import react.RComponent
import react.RProps
import react.RState

@KoinApiExtension
abstract class KoinReactComponent<Props: RProps, State: RState>(
    private val module: Module,
): RComponent<Props, State>(), KoinComponent, CoroutineScope by MainScope() {
    final override fun getKoin() = koinApp.koin

    init { getKoin().loadModules(listOf(module)) }

    override fun componentWillUnmount() {
        getKoin().unloadModules(listOf(module))
    }
}
