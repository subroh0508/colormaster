package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.utilities.LoadState
import net.subroh0508.colormaster.utilities.ViewModel

abstract class SearchViewModel<T: SearchParams>(
    protected val idolColorsRepository: IdolColorsRepository,
    emptyParams: T,
    coroutineScope: CoroutineScope? = null,
) : ViewModel(coroutineScope) {
    @ExperimentalCoroutinesApi
    protected val searchParams: MutableStateFlow<T> by lazy { MutableStateFlow(emptyParams) }
    @ExperimentalCoroutinesApi
    protected val idolsLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Loaded<List<IdolColor>>(listOf())) }
    @ExperimentalCoroutinesApi
    protected val selected: MutableStateFlow<List<String>> by lazy { MutableStateFlow(listOf()) }
    @ExperimentalCoroutinesApi
    protected val favorites: MutableStateFlow<List<String>> by lazy { MutableStateFlow(listOf()) }

    private val items: List<IdolColor> get() = idolsLoadState.value.getValueOrNull() ?: listOf()

    @FlowPreview
    @ExperimentalCoroutinesApi
    abstract val uiModel: Flow<SearchUiModel>

    open fun search() {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { search(searchParams.value) }
                .onSuccess { idolsLoadState.value = LoadState.Loaded(it) }
                .onFailure {
                    it.printStackTrace()
                    idolsLoadState.value = LoadState.Error(it)
                }
        }

        startLoading()
        job.start()
    }

    fun setSearchParams(params: SearchParams) {
        if (searchParams.value == params) return

        @Suppress("UNCHECKED_CAST")
        searchParams.value = params as T
        search()
    }

    protected abstract suspend fun search(params: T): List<IdolColor>

    fun select(item: IdolColor, selected: Boolean) { this.selected.value = if (selected) this.selected.value + listOf(item.id) else this.selected.value - listOf(item.id) }
    fun selectAll(selected: Boolean) { this.selected.value = if (selected) items.map(IdolColor::id) else listOf() }

    fun loadFavorites() {
        viewModelScope.launch { favorites.value = idolColorsRepository.getFavoriteIdolIds() }
    }

    fun favorite(id: String) {
        viewModelScope.launch {
            idolColorsRepository.favorite(id)
            favorites.value = idolColorsRepository.getFavoriteIdolIds()
        }
    }

    fun unfavorite(id: String) {
        viewModelScope.launch {
            idolColorsRepository.unfavorite(id)
            favorites.value = idolColorsRepository.getFavoriteIdolIds()
        }
    }

    protected fun startLoading() {
        idolsLoadState.value = LoadState.Loading
        selected.value = listOf()
    }
}
