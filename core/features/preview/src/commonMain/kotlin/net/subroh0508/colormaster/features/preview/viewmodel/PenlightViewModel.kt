package net.subroh0508.colormaster.features.preview.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.subroh0508.colormaster.model.PreviewRepository

class PenlightViewModel(
    private val withDescription: Boolean,
    private val repository: PreviewRepository,
) : ViewModel() {
    val uiState: StateFlow<PenlightUiState> = repository.getPreviewColorsStream()
        .map { idols ->
            when (idols.isEmpty()) {
                true -> PenlightUiState.Loading
                false -> PenlightUiState.Loaded(idols, withDescription)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PenlightUiState.Loading,
        )

    suspend fun show(ids: List<String>) {
        repository.clear()
        repository.show(ids, "ja")
    }
}
