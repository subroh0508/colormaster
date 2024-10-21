package net.subroh0508.colormaster.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import net.subroh0508.colormaster.features.search.model.SearchParams
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolColorsRepository

class SearchIdolsViewModel(
    private val repository: IdolColorsRepository,
) : ViewModel() {
    private val searchParamsStateFlow = MutableStateFlow<SearchParams>(SearchParams.None)
    private val eventStateFlow = MutableStateFlow<IdolSelectUiEvent>(IdolSelectUiEvent.UnselectAll)

    val uiState: StateFlow<SearchIdolsUiState> = combine(
        searchParamsStateFlow.onEach { params ->
            when (params) {
                is SearchParams.None -> repository.rand(limit = 10, "ja")
                is SearchParams.ByName -> searchByName(params)
                is SearchParams.ByLive -> searchByLive(params)
            }
        },
        eventStateFlow,
        repository.getIdolColorsStream(limit = 10, "ja"),
        repository.getInChargeOfIdolIdsStream(),
        repository.getFavoriteIdolIdsStream(),
    ) { params, event, idolColors, inChargeOfIds, favoriteIds ->
        buildUiState(params, event, idolColors, inChargeOfIds, favoriteIds)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchIdolsUiState.Loading(SearchParams.None),
    )

    fun search(params: SearchParams) {
        searchParamsStateFlow.value = params
    }

    fun select(id: String, selected: Boolean) {
        eventStateFlow.value = when (selected) {
            true -> IdolSelectUiEvent.Select(id)
            false -> IdolSelectUiEvent.Unselect(id)
        }
    }

    fun selectAll(selected: Boolean) {
        eventStateFlow.value = when (selected) {
            true -> IdolSelectUiEvent.SelectAll
            false -> IdolSelectUiEvent.UnselectAll
        }
    }

    private suspend fun searchByName(params: SearchParams.ByName) {
        if (params.isEmpty()) {
            repository.rand(limit = 10, "ja")
            return
        }

        repository.search(params.idolName, params.brands, params.types, "ja")
    }

    private suspend fun searchByLive(params: SearchParams.ByLive) {
        val liveName = params.liveName
        if (liveName == null) {
            repository.refresh()
            return
        }

        repository.search(liveName, "ja")
    }

    private fun buildUiState(
        params: SearchParams,
        event: IdolSelectUiEvent,
        idolColors: List<IdolColor>,
        inChargeOfIds: List<String>,
        favoriteIds: List<String>,
    ): SearchIdolsUiState {
        val items = idolColors.map { idolColor ->
            IdolColorListItem(
                idolColor,
                selected = when (event) {
                    is IdolSelectUiEvent.Select -> event.id == idolColor.id
                    is IdolSelectUiEvent.Unselect -> event.id != idolColor.id
                    is IdolSelectUiEvent.UnselectAll -> false
                    is IdolSelectUiEvent.SelectAll -> true
                },
                inCharge = idolColor.id in inChargeOfIds,
                favorite = idolColor.id in favoriteIds,
            )
        }

        return SearchIdolsUiState.Loaded(params, items)
    }

    private sealed interface IdolSelectUiEvent {
        data class Select(val id: String) : IdolSelectUiEvent
        data class Unselect(val id: String) : IdolSelectUiEvent
        data object UnselectAll : IdolSelectUiEvent
        data object SelectAll : IdolSelectUiEvent
    }
}