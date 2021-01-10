package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.LiveRepository
import net.subroh0508.colormaster.utilities.LoadState

class SearchByLiveViewModel(
    private val liveRepository: LiveRepository,
    idolColorsRepository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : SearchViewModel<SearchParams.ByLive>(idolColorsRepository, SearchParams.ByLive.EMPTY, coroutineScope) {
    @ExperimentalCoroutinesApi
    private val liveLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Loaded<List<LiveName>>(listOf())) }

    @ExperimentalCoroutinesApi
    override val uiModel: Flow<SearchUiModel>
        get() = combine(
            searchParams,
            idolsLoadState,
            liveLoadState,
            selected,
            favorites,
        ) { params, idolsLoadState, liveLoadState, selected, favorites ->
            SearchUiModel(params, idolsLoadState, liveLoadState, selected, favorites)
        }.distinctUntilChanged().apply { launchIn(viewModelScope) }

    override fun search() = when {
        searchParams.value.isEmpty() -> Unit
        searchParams.value.liveName == null -> fetchLiveNameSuggests()
        else -> super.search()
    }

    override suspend fun search(params: SearchParams.ByLive) = params.liveName?.let {
        idolColorsRepository.search(it)
    } ?: listOf()

    private fun fetchLiveNameSuggests() {
        val (_, query, date) = searchParams.value

        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching {
                when {
                    // TODO return date range from today
                    date != null -> date.range()?.let { liveRepository.suggest(it) } ?: listOf()
                    query != null -> liveRepository.suggest(query)
                    else -> listOf()
                }
            }
                .onSuccess { liveLoadState.value = LoadState.Loaded(it) }
                .onFailure { liveLoadState.value = LoadState.Error(it) }
        }

        liveLoadState.value = LoadState.Loading
        job.start()
    }
}
