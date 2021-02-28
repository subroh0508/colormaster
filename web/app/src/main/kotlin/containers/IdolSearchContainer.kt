package containers

import KoinReactComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByLiveViewModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchViewModel
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import pages.IdolSearchPage
import react.*
import react.router.dom.useHistory
import toPenlight
import toPreview
import useQuery

@Suppress("FunctionName")
fun RBuilder.IdolSearchContainer() = child(IdolSearchContainerImpl)

private val IdolSearchContainerImpl = functionalComponent<RProps> {
    val history = useHistory()
    val tab = SearchByTab.findByQuery(useQuery().get("by"))

    when (tab) {
        SearchByTab.BY_NAME -> child(SearchByNameContainerComponent::class) {
            attrs.showPreview = { items -> history.toPreview(items.joinToString("&") { "id=${it.id}" }) }
            attrs.showPenlight = { items -> history.toPenlight(items.joinToString("&") { "id=${it.id}" }) }
        }
        SearchByTab.BY_LIVE -> child(SearchByLiveContainerComponent::class) {
            attrs.showPreview = { items -> history.toPreview(items.joinToString("&") { "id=${it.id}" }) }
            attrs.showPenlight = { items -> history.toPenlight(items.joinToString("&") { "id=${it.id}" }) }
        }
    }
}

private class SearchByNameContainerComponent : IdolSearchContainerComponent<SearchParams.ByName, SearchByNameViewModel>(
    module {
        single { SearchByNameViewModel(get()) }
    }
) {
    override val viewModel: SearchByNameViewModel by inject()
    override fun onChangeQuery(query: String?) = viewModel.changeIdolNameSearchQuery(query)
}

private class SearchByLiveContainerComponent : IdolSearchContainerComponent<SearchParams.ByLive, SearchByLiveViewModel>(
    module {
        single { SearchByLiveViewModel(get(), get()) }
    }
) {
    override val viewModel: SearchByLiveViewModel by inject()
    override fun onChangeQuery(query: String?) = viewModel.changeLiveNameSuggestQuery(query)
}

private abstract class IdolSearchContainerComponent<T: SearchParams, out VM: SearchViewModel<T>>(
    module: Module,
) : KoinReactComponent<IdolSearchProps, IdolSearchState>(module) {
    protected abstract val viewModel: VM
    protected abstract fun onChangeQuery(query: String?)

    override fun IdolSearchState.init() {
        uiModel = SearchUiModel.ByName.INITIALIZED
    }

    override fun componentDidMount() {
        viewModel.uiModel.onEach {
            setState { uiModel = it }
        }.launchIn(this)

        viewModel.search()
    }

    override fun RBuilder.render() {
        IdolSearchPage {
            attrs.model = state.uiModel
            attrs.onChangeSearchParams = viewModel::setSearchParams
            attrs.onChangeSearchQuery = this@IdolSearchContainerComponent::onChangeQuery
            attrs.onClickIdolColor = viewModel::select
            attrs.onClickSelectAll = viewModel::selectAll
            attrs.onDoubleClickIdolColor = { item -> props.showPenlight(listOf(item)) }
            attrs.onClickPreview = { props.showPreview(selectedItems) }
            attrs.onClickPenlight = { props.showPenlight(selectedItems) }
        }
    }

    private val params: SearchParams get() = state.uiModel.params
    private val selectedItems: List<IdolColor> get() = state.uiModel.selectedItems
}

private external interface IdolSearchProps: RProps {
    var showPreview: (items: List<IdolColor>) -> Unit
    var showPenlight: (items: List<IdolColor>) -> Unit
}

private external interface IdolSearchState : RState {
    var uiModel: SearchUiModel
}
