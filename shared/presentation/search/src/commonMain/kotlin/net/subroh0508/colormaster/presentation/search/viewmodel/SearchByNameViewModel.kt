package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import net.subroh0508.colormaster.model.toIdolName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.presentation.common.LoadState

class SearchByNameViewModel(
    repository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : SearchViewModel<SearchParams.ByName>(repository, SearchParams.ByName.EMPTY, coroutineScope) {
    override val uiModel: Flow<SearchUiModel>
        get() = combine(
            searchParams,
            idolsLoadState,
            selected,
            inCharges,
            favorites,
        ) { params, loadState, selected, inCharges, favorites ->
            SearchUiModel(params, loadState, selected, inCharges, favorites)
        }.distinctUntilChanged()

    fun changeIdolNameSearchQuery(query: String?) = setSearchParams(searchParams.value.change(query.toIdolName()))

    override fun search() =
        if (searchParams.value.isEmpty())
            loadRandom()
        else
            super.search()

    override suspend fun search(params: SearchParams.ByName) = idolColorsRepository.search(
        params.idolName, params.brands, params.types, "ja",
    )

    private fun loadRandom() {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { idolColorsRepository.rand(10, "ja") }
                .onSuccess { idolsLoadState.value = LoadState.Loaded(it) }
                .onFailure { idolsLoadState.value = LoadState.Error(it) }
        }

        startLoading()
        job.start()
    }
}
