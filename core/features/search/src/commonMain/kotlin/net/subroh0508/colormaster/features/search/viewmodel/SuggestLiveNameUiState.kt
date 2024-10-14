package net.subroh0508.colormaster.features.search.viewmodel

import net.subroh0508.colormaster.model.LiveName

sealed interface SuggestLiveNameUiState {
    data object Loading : SuggestLiveNameUiState
    data class Loaded(
        val suggests: List<LiveName>,
    ) : SuggestLiveNameUiState
    data class Error(
        val error: Throwable,
    ) : SuggestLiveNameUiState
}
