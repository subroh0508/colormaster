@file:Suppress("FunctionName")

package containers

import KoinAppContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByLiveViewModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchViewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pages.IdolSearchPage
import react.*
import react.router.dom.useHistory
import toPenlight
import toPreview
import useQuery

fun RBuilder.IdolSearchContainer() = child(IdolSearchContainerComponent)

private val IdolSearchContainerComponent = functionalComponent<RProps> {
    val component = SearchByTab.findByQuery(useQuery().get("by")).let { tab ->
        when (tab) {
            SearchByTab.BY_NAME -> SearchByNameComponent
            SearchByTab.BY_LIVE -> SearchByLiveComponent
        }
    }

    child(component)
}

private const val SEARCH_BY_NAME_SCOPE = "SEARCH_BY_NAME_SCOPE"
private const val SEARCH_BY_LIVE_SCOPE = "SEARCH_BY_LIVE_SCOPE"

private val SearchByNameContext = createContext<SearchByNameViewModel>()
private val SearchByLiveContext = createContext<SearchByLiveViewModel>()

private val SearchByNameComponent get() = IdolSearchContextProviderContainer(
    SearchByNameContext,
    SEARCH_BY_NAME_SCOPE,
    SearchUiModel.ByName.INITIALIZED,
    SearchByNameViewModel::changeIdolNameSearchQuery,
) { scope ->
    module {
        scope(named(SEARCH_BY_NAME_SCOPE)) {
            scoped { SearchByNameViewModel(get(), scope) }
        }
    }
}

private val SearchByLiveComponent get() = IdolSearchContextProviderContainer(
    SearchByLiveContext,
    SEARCH_BY_LIVE_SCOPE,
    SearchUiModel.ByLive.INITIALIZED,
    SearchByLiveViewModel::changeLiveNameSuggestQuery,
) { scope ->
    module {
        scope(named(SEARCH_BY_LIVE_SCOPE)) {
            scoped { SearchByLiveViewModel(get(), get(), scope) }
        }
    }
}

private inline fun <T: SearchParams, reified VM: SearchViewModel<T>> IdolSearchContextProviderContainer(
    context: RContext<VM>,
    scopeId: String,
    init: SearchUiModel,
    noinline onChangeQuery: VM.(String?) -> Unit,
    crossinline module: (CoroutineScope) -> Module,
) = functionalComponent<RProps> { props ->
    val (koinApp, appScope) = useContext(KoinAppContext)
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

    viewModel ?: return@functionalComponent

    context.Provider {
        attrs.value = viewModel

        child(IdolSearchComponent(context, init, onChangeQuery))
    }
}


private fun <T: SearchParams, VM: SearchViewModel<T>> IdolSearchComponent(
    context: RContext<VM>,
    init: SearchUiModel,
    onChangeQuery: VM.(String?) -> Unit,
) = functionalComponent<RProps> {
    val history = useHistory()

    val (_, appScope) = useContext(KoinAppContext)
    val viewModel = useContext(context)

    val (uiModel, setUiModel) = useState(init)

    useEffectOnce {
        viewModel.uiModel.onEach {
            setUiModel(value = it)
        }.launchIn(appScope)

        viewModel.search()
    }

    fun query(items: List<IdolColor>) = items.joinToString("&") { "id=${it.id}" }

    IdolSearchPage {
        attrs.model = uiModel
        attrs.onChangeSearchParams = viewModel::setSearchParams
        attrs.onChangeSearchQuery = { viewModel.onChangeQuery(it) }
        attrs.onClickIdolColor = viewModel::select
        attrs.onClickSelectAll = viewModel::selectAll
        attrs.onDoubleClickIdolColor = { item -> history.toPenlight(query(listOf(item))) }
        attrs.onClickPreview = { history.toPreview(query(uiModel.selectedItems)) }
        attrs.onClickPenlight = { history.toPenlight(query(uiModel.selectedItems)) }
    }
}
