package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.ManualSearchUiModel
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.utilities.LoadState
import net.subroh0508.colormaster.utilities.ViewModel

class IdolSearchViewModel(
    private val repository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : ViewModel(coroutineScope) {
    @ExperimentalCoroutinesApi
    private val _searchParams: MutableStateFlow<SearchParams> by lazy { MutableStateFlow(SearchParams.EMPTY) }
    @ExperimentalCoroutinesApi
    private val _idolsLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Loaded<List<IdolColor>>(listOf())) }
    @ExperimentalCoroutinesApi
    private val _selected: MutableStateFlow<List<String>> by lazy { MutableStateFlow(listOf()) }
    @ExperimentalCoroutinesApi
    private val _favorites: MutableStateFlow<List<String>> by lazy { MutableStateFlow(listOf()) }

    private val items: List<IdolColor> get() = _idolsLoadState.value.getValueOrNull() ?: listOf()

    @FlowPreview
    @ExperimentalCoroutinesApi
    val uiModel: Flow<ManualSearchUiModel>
        get() = combine(
            _searchParams,
            _idolsLoadState,
            _selected,
            _favorites,
        ) { params, loadState, selected, favorites ->
            ManualSearchUiModel(params, loadState, selected, favorites)
        }.distinctUntilChanged().apply { launchIn(viewModelScope) }

    fun loadRandom() {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.rand(10) }
                .onSuccess { _idolsLoadState.value = LoadState.Loaded(it) }
                .onFailure {
                    it.printStackTrace()
                    _idolsLoadState.value = LoadState.Error(it)
                }
        }

        startLoading()
        job.start()
    }

    fun search() {
        if (searchParams.isEmpty()) {
            loadRandom()
            return
        }

        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.search(searchParams.idolName, searchParams.brands, searchParams.types) }
                .onSuccess { _idolsLoadState.value = LoadState.Loaded(it) }
                .onFailure {
                    it.printStackTrace()
                    _idolsLoadState.value = LoadState.Error(it)
                }
        }

        startLoading()
        job.start()
    }

    var searchParams: SearchParams
        get() = _searchParams.value
        set(value) {
            if (_searchParams.value == value) return

            _searchParams.value = value
            search()
        }

    fun select(item: IdolColor, selected: Boolean) { _selected.value = if (selected) _selected.value + listOf(item.id) else _selected.value - listOf(item.id) }
    fun selectAll(selected: Boolean) { _selected.value = if (selected) items.map(IdolColor::id) else listOf() }

    fun loadFavorites() {
        viewModelScope.launch { _favorites.value = repository.getFavoriteIdolIds() }
    }

    fun favorite(id: String) {
        viewModelScope.launch {
            repository.favorite(id)
            _favorites.value = repository.getFavoriteIdolIds()
        }
    }

    fun unfavorite(id: String) {
        viewModelScope.launch {
            repository.unfavorite(id)
            _favorites.value = repository.getFavoriteIdolIds()
        }
    }

    private fun startLoading() {
        _idolsLoadState.value = LoadState.Loading
        _selected.value = listOf()
    }
}
