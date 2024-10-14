package net.subroh0508.colormaster.features.search.viewmodel

import net.subroh0508.colormaster.features.search.model.SearchParams
import net.subroh0508.colormaster.model.IdolColor

sealed interface SearchIdolsUiState {
    val params: SearchParams

    data class Loading(
        override val params: SearchParams,
    ) : SearchIdolsUiState
    data class Loaded(
        override val params: SearchParams,
        val idols: List<IdolColorListItem>,
    ) : SearchIdolsUiState
    data class Error(
        override val params: SearchParams,
        val error: Throwable,
    ) : SearchIdolsUiState
}

data class IdolColorListItem(
    val item: IdolColor,
    val selected: Boolean = false,
    val inCharge: Boolean = false,
    val favorite: Boolean = false,
)
