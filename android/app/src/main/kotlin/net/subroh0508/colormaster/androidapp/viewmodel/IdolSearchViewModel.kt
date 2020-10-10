package net.subroh0508.colormaster.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.UiModel
import net.subroh0508.colormaster.model.UiModel.Search.IdolColorItem
import net.subroh0508.colormaster.model.ui.commons.LoadState
import net.subroh0508.colormaster.model.ui.idol.Filters
import net.subroh0508.colormaster.repository.IdolColorsRepository

class IdolSearchViewModel(
    private val repository: IdolColorsRepository,
) : ViewModel() {
    @ExperimentalCoroutinesApi
    private val _searchParams: MutableStateFlow<Filters> by lazy { MutableStateFlow(Filters.Empty) }
    @ExperimentalCoroutinesApi
    private val _idolsLoadState: MutableStateFlow<LoadState<List<IdolColor>>> by lazy { MutableStateFlow(LoadState.Loaded(listOf())) }

    @FlowPreview
    @ExperimentalCoroutinesApi
    val uiModel: Flow<UiModel.Search>
        get() = combine(_searchParams, _idolsLoadState) { params, loadState ->
            UiModel.Search(
                loadState.getValueOrNull()?.map(::IdolColorItem) ?: listOf(),
                params,
                loadState.getErrorOrNull(),
                loadState.isLoading,
            )
        }.apply { launchIn(viewModelScope) }

    fun loadRandom() {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.rand(10) }
                .onSuccess { _idolsLoadState.value = LoadState.Loaded(it) }
                .onFailure {
                    it.printStackTrace()
                    _idolsLoadState.value = LoadState.Error(it)
                }
        }

        _idolsLoadState.value = LoadState.Loading
        job.start()
    }

    fun search() {
        if (searchParams == Filters.Empty) {
            loadRandom()
            return
        }

        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.search(searchParams.idolName, searchParams.title, searchParams.types) }
                .onSuccess { _idolsLoadState.value = LoadState.Loaded(it) }
                .onFailure {
                    it.printStackTrace()
                    _idolsLoadState.value = LoadState.Error(it)
                }
        }

        _idolsLoadState.value = LoadState.Loading
        job.start()
    }

    var searchParams: Filters
        get() = _searchParams.value
        set(value) {
            _searchParams.value = value
            search()
        }
}
