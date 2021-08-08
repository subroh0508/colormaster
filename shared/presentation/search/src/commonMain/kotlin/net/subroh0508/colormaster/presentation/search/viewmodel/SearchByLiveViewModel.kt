package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.LiveRepository
import net.subroh0508.colormaster.presentation.common.LoadState

class SearchByLiveViewModel(
    private val liveRepository: LiveRepository,
    idolColorsRepository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : SearchViewModel<SearchParams.ByLive>(idolColorsRepository, SearchParams.ByLive.EMPTY, coroutineScope) {
    private val liveLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Loaded<List<LiveName>>(listOf())) }

    override val uiModel: Flow<SearchUiModel>
        get() = combine(
            searchParams,
            idolsLoadState,
            liveLoadState,
            selected,
            inCharges,
            favorites,
        ) { args ->
            SearchUiModel(
                args[0] as SearchParams.ByLive,
                args[1] as LoadState,
                args[2] as LoadState,
                args[3] as List<String>,
                args[4] as List<String>,
                args[5] as List<String>,
            )
        }.distinctUntilChanged().apply { launchIn(viewModelScope) }

    fun changeLiveNameSuggestQuery(rawQuery: String?) = setSearchParams(searchParams.value.change(rawQuery))

    override fun search() = when {
        searchParams.value.isEmpty() -> {
            clearLiveLoadState()
            clearIdolLoadState()
        }
        searchParams.value.liveName == null -> {
            clearIdolLoadState()
            fetchLiveNameSuggests()
        }
        else -> {
            clearLiveLoadState()
            super.search()
        }
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
                .onSuccess {
                    if (isQueryExactlyLiveName(query, it)) {
                        clearLiveLoadState()
                        setSearchParams(searchParams.value.select(it[0]))
                        return@onSuccess
                    }

                    liveLoadState.value = LoadState.Loaded(it)
                }
                .onFailure { liveLoadState.value = LoadState.Error(it) }
        }

        liveLoadState.value = LoadState.Loading
        job.start()
    }

    private fun clearIdolLoadState() { idolsLoadState.value = LoadState.Loaded(listOf<IdolColor>()) }
    private fun clearLiveLoadState() { liveLoadState.value = LoadState.Loaded(listOf<LiveName>()) }

    private fun isQueryExactlyLiveName(query: String?, suggestions: List<LiveName>) =
        query != null && query == suggestions.firstOrNull()?.value && suggestions.size == 1
}
