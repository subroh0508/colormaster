package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.LiveRepository
import net.subroh0508.colormaster.utilities.LoadState

class SearchByLiveViewModel(
    private val liveRepository: LiveRepository,
    idolColorsRepository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : SearchViewModel<SearchParams.ByLive>(idolColorsRepository, SearchParams.ByLive.EMPTY, coroutineScope) {
    @ExperimentalCoroutinesApi
    private val _liveLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Loaded<List<LiveName>>(listOf())) }

    override suspend fun search(params: SearchParams.ByLive): List<IdolColor> {
        fetchLiveNameSuggests()

        return params.liveName?.let { idolColorsRepository.search(it) } ?: listOf()
    }

    private fun fetchLiveNameSuggests() {
        val (_, query, date) = searchParams

        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching {
                when {
                    date != null -> date.range()?.let { liveRepository.suggest(it) } ?: listOf()
                    query != null -> liveRepository.suggest(query)
                    else -> listOf()
                }
            }
                .onSuccess { _liveLoadState.value = LoadState.Loaded(it) }
                .onFailure { _liveLoadState.value = LoadState.Error(it) }
        }

        _liveLoadState.value = LoadState.Loading
        job.start()
    }
}
