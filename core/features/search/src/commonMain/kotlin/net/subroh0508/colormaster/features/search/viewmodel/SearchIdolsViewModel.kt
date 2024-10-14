package net.subroh0508.colormaster.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import net.subroh0508.colormaster.features.search.model.SearchParams
import net.subroh0508.colormaster.model.IdolColorsRepository

class SearchIdolsViewModel(
    private val repository: IdolColorsRepository,
) : ViewModel() {
    val uiState: StateFlow<SearchIdolsUiState> = combine(
        repository.getIdolColorsStream(limit = 10, "ja"),
        repository.getInChargeOfIdolIdsStream(),
        repository.getFavoriteIdolIdsStream(),
    ) { idolColors, inChargeOfIds, favoriteIds ->
        SearchIdolsUiState.Loaded(
            SearchParams.None,
            idolColors.map { idolColor ->
                IdolColorListItem(
                    idolColor,
                    selected = false,
                    inCharge = idolColor.id in inChargeOfIds,
                    favorite = idolColor.id in favoriteIds,
                )
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchIdolsUiState.Loading(SearchParams.None),
    )

    suspend fun search(params: SearchParams) = when (params) {
        is SearchParams.None -> Unit
        is SearchParams.ByName -> searchByName(params)
        is SearchParams.ByLive -> searchByLive(params)
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
}