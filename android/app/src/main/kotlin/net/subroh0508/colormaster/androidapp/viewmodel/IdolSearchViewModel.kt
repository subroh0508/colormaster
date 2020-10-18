package net.subroh0508.colormaster.androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.ManualSearchUiModel
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.utilities.LoadState

class IdolSearchViewModel(
    private val repository: IdolColorsRepository,
) : ViewModel() {
    @ExperimentalCoroutinesApi
    private val _searchParams: MutableStateFlow<SearchParams> by lazy { MutableStateFlow(SearchParams.EMPTY) }
    @ExperimentalCoroutinesApi
    private val _idolsLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Loaded<List<IdolColor>>(listOf())) }

    /*
    @FlowPreview
    @ExperimentalCoroutinesApi
    val uiModel: Flow<ManualSearchUiModel>
        get() = combine(_searchParams, _idolsLoadState) { params, loadState ->
            ManualSearchUiModel(params, loadState)
        }.apply { launchIn(viewModelScope) }
    */

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

        _idolsLoadState.value = LoadState.Loading
        job.start()
    }

    var searchParams: SearchParams
        get() = _searchParams.value
        set(value) {
            _searchParams.value = value
            search()
        }
}
