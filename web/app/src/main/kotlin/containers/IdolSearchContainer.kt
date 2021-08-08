@file:Suppress("FunctionName")

package containers

import AuthenticationProviderContext
import KoinComponent
import KoinContext
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
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pages.IdolSearchPage
import react.*
import react.router.dom.useHistory
import toPenlight
import toPreview
import useQuery

fun RBuilder.IdolSearchContainer() = child(IdolSearchContextProvider)

private val IdolSearchContextProvider = functionalComponent<RProps> {
    val component = SearchByTab.findByQuery(useQuery().get("by")).let { tab ->
        when (tab) {
            SearchByTab.BY_NAME -> SearchByNameContextProvider
            SearchByTab.BY_LIVE -> SearchByLiveContextProvider
        }
    }

    child(component)
}

private const val SEARCH_BY_NAME_SCOPE = "SEARCH_BY_NAME_SCOPE"
private const val SEARCH_BY_LIVE_SCOPE = "SEARCH_BY_LIVE_SCOPE"

private val SearchByNameContext = createContext<SearchByNameViewModel>()
private val SearchByLiveContext = createContext<SearchByLiveViewModel>()

private fun byNameModule(scope: CoroutineScope) = module {
    scope(named(SEARCH_BY_NAME_SCOPE)) {
        scoped { SearchByNameViewModel(get(), scope) }
    }
}

private val SearchByNameContextProvider get() = KoinComponent(
    SearchByNameContext,
    SEARCH_BY_NAME_SCOPE,
    ::byNameModule,
) {
    child(IdolSearchContainer(
        SearchByNameContext,
        SearchUiModel.ByName.INITIALIZED,
        SearchByNameViewModel::changeIdolNameSearchQuery,
    ))
}

private fun byLiveModule(scope: CoroutineScope) = module {
    scope(named(SEARCH_BY_LIVE_SCOPE)) {
        scoped { SearchByLiveViewModel(get(), get(), scope) }
    }
}

private val SearchByLiveContextProvider get() = KoinComponent(
    SearchByLiveContext,
    SEARCH_BY_LIVE_SCOPE,
    ::byLiveModule,
) {
    child(IdolSearchContainer(
        SearchByLiveContext,
        SearchUiModel.ByLive.INITIALIZED,
        SearchByLiveViewModel::changeLiveNameSuggestQuery,
    ))
}

private fun <T: SearchParams, VM: SearchViewModel<T>> IdolSearchContainer(
    context: RContext<VM>,
    init: SearchUiModel,
    onChangeQuery: VM.(String?) -> Unit,
) = functionalComponent<RProps> {
    val history = useHistory()

    val (_, appScope) = useContext(KoinContext)
    val currentUser = useContext(AuthenticationProviderContext)
    val viewModel = useContext(context)

    val (uiModel, setUiModel) = useState(init)

    useEffectOnce {
        viewModel.uiModel.onEach {
            setUiModel(value = it)
        }.launchIn(appScope)

        viewModel.search()
    }

    useEffect(currentUser) {
        if (currentUser == null) return@useEffect

        viewModel.loadFavorites()
    }

    fun query(items: List<IdolColor>) = items.joinToString("&") { "id=${it.id}" }

    IdolSearchPage {
        attrs.model = uiModel
        attrs.onChangeSearchParams = viewModel::setSearchParams
        attrs.onChangeSearchQuery = { viewModel.onChangeQuery(it) }
        attrs.onClickIdolColor = viewModel::select
        attrs.onClickSelectAll = viewModel::selectAll
        attrs.onDoubleClickIdolColor = { item -> history.toPenlight(query(listOf(item))) }
        attrs.onInChargeClickIdolColor = { (id), inCharge -> viewModel.registerInChargeOf(id, inCharge) }
        attrs.onFavoriteClickIdolColor = { (id), favorite -> viewModel.favorite(id, favorite) }
        attrs.onClickPreview = { history.toPreview(query(uiModel.selectedItems)) }
        attrs.onClickPenlight = { history.toPenlight(query(uiModel.selectedItems)) }
    }
}
