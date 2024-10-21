package net.subroh0508.colormaster.features.preview.viewmodel

import net.subroh0508.colormaster.model.IdolColor

sealed interface PenlightUiState {
    data object Loading : PenlightUiState
    data class Loaded(
        val idols: List<IdolColor>,
        val withDescription: Boolean,
    ) : PenlightUiState
}