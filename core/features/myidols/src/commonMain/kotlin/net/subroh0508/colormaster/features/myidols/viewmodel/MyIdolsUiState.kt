package net.subroh0508.colormaster.features.myidols.viewmodel

import net.subroh0508.colormaster.model.IdolColor

sealed interface MyIdolsUiState {
    data object Loading : MyIdolsUiState
    data class Loaded(
        val inChargeIdols: List<IdolColor>,
        val favoriteIdols: List<IdolColor>,
    ) : MyIdolsUiState
    data class Error(
        val error: Throwable,
    ) : MyIdolsUiState
}
