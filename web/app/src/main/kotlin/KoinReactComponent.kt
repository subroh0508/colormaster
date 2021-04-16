import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.newScope
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import react.RComponent
import react.RProps
import react.RState

abstract class KoinReactComponent<Props: RProps, State: RState>(
    private val module: Module,
): RComponent<Props, State>(), KoinScopeComponent, CoroutineScope by MainScope() {
    final override fun getKoin() = koinApp.koin
    override val scope: Scope by newScope()

    init { getKoin().loadModules(listOf(module)) }

    override fun componentWillUnmount() {
        getKoin().unloadModules(listOf(module))
    }
}
