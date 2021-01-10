package containers

import appDI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.presentation.search.model.ManualSearchUiModel
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import pages.IdolSearchPage
import react.*
import react.router.dom.useHistory
import toPenlight
import toPreview

@Suppress("FunctionName")
fun RBuilder.IdolSearchContainer() = child(IdolSearchContainerImpl) {}

private val IdolSearchContainerImpl = functionalComponent<RProps> {
    val history = useHistory()

    child(IdolSearchContainerComponent::class) {
        attrs.showPreview = { items -> history.toPreview(items.joinToString("&") { "id=${it.id}" }) }
        attrs.showPenlight = { items -> history.toPenlight(items.joinToString("&") { "id=${it.id}" }) }
    }
}

private class IdolSearchContainerComponent :
    RComponent<IdolSearchProps, IdolSearchState>(), KoinComponent, CoroutineScope by MainScope() {
    override fun getKoin() = appDI.koin

    private val viewModel: SearchByNameViewModel by inject()

    override fun IdolSearchState.init() {
        uiModel = ManualSearchUiModel.INITIALIZED
    }

    override fun componentDidMount() {
        launch {
            viewModel.uiModel.collect {
                setState { uiModel = it }
            }
        }

        viewModel.search()
    }

    override fun RBuilder.render() {
        IdolSearchPage {
            attrs.model = state.uiModel
            attrs.onChangeIdolName = { name -> viewModel.searchParams = change(params, name.toIdolName()) }
            attrs.onSelectTitle = { brands, checked -> viewModel.searchParams = change(params, brands, checked) }
            attrs.onSelectType = { type, checked -> viewModel.searchParams = change(params, type, checked) }
            attrs.onClickIdolColor = viewModel::select
            attrs.onClickSelectAll = viewModel::selectAll
            attrs.onDoubleClickIdolColor = { item -> props.showPenlight(listOf(item)) }
            attrs.onClickPreview = { props.showPreview(selectedItems) }
            attrs.onClickPenlight = { props.showPenlight(selectedItems) }
        }
    }

    private fun change(params: SearchParams, idolName: IdolName?) = when (params) {
        is SearchParams.ByName -> params.change(idolName)
        else -> params
    }
    private fun change(params: SearchParams, brands: Brands?, checked: Boolean) = when (params) {
        is SearchParams.ByName -> params.change(if (checked) brands else null)
        else -> params
    }
    private fun change(params: SearchParams, type: Types, checked: Boolean) = when (params) {
        is SearchParams.ByName -> params.change(type, checked)
        else -> params
    }

    private val params: SearchParams get() = state.uiModel.params
    private val selectedItems: List<IdolColor> get() = state.uiModel.selectedItems
}

private external interface IdolSearchProps: RProps {
    var showPreview: (items: List<IdolColor>) -> Unit
    var showPenlight: (items: List<IdolColor>) -> Unit
}

private external interface IdolSearchState : RState {
    var uiModel: ManualSearchUiModel
}
