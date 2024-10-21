package net.subroh0508.colormaster.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.subroh0508.colormaster.features.search.model.LiveNameQuery
import net.subroh0508.colormaster.model.LiveRepository

class SuggestLiveNameViewModel(
    private val repository: LiveRepository,
) : ViewModel() {
    val uiState: StateFlow<SuggestLiveNameUiState> = repository.getLiveNamesStream()
        .map { liveNames ->
            SuggestLiveNameUiState.Loaded(liveNames)
        }.catch { e ->
            SuggestLiveNameUiState.Error(e)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SuggestLiveNameUiState.Loaded(listOf()),
        )

    suspend fun suggest(liveNameQuery: LiveNameQuery) {
        val (query, isSettled) = liveNameQuery

        if (query == null || isSettled) {
            repository.refresh()
            return
        }

        if (liveNameQuery.isNumber()) {
            val dateRange = liveNameQuery.toDateNum()?.range() ?: return
            repository.suggest(dateRange)
        }

        repository.suggest(query)
    }
}
