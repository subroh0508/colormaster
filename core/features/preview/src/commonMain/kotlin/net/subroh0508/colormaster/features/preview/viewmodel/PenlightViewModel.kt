package net.subroh0508.colormaster.features.preview.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.subroh0508.colormaster.model.IdolColorsRepository

class PenlightViewModel(
    private val ids: List<String>,
    private val withDescription: Boolean,
    private val repository: IdolColorsRepository,
) : ViewModel() {
    val uiState: StateFlow<PenlightUiState> = flow {
        emit(repository.search(ids, "ja"))
    }.map { idols ->
        PenlightUiState.Loaded(idols, withDescription)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PenlightUiState.Loading,
    )
}