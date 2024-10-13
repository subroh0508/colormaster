package net.subroh0508.colormaster.features.myidols.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolColorsRepository

class MyIdolsViewModel(
    private val repository: IdolColorsRepository,
) : ViewModel() {
    val uiState: StateFlow<MyIdolsUiState> = combine<
        List<IdolColor>,
        List<IdolColor>,
        MyIdolsUiState,
    >(
        repository.getInChargeOfIdolIdsStream()
            .map { ids -> repository.search(ids, "ja") },
        repository.getFavoriteIdolIdsStream()
            .map { ids -> repository.search(ids, "ja") },
    ) { inCharges, favorites ->
        MyIdolsUiState.Loaded(inCharges, favorites)
    }.catch { e ->
        emit(MyIdolsUiState.Error(e))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MyIdolsUiState.Loading,
    )
}
