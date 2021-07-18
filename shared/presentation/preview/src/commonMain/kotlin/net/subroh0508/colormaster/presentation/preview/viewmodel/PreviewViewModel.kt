package net.subroh0508.colormaster.presentation.preview.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.common.ViewModel

class PreviewViewModel(
    private val repository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : ViewModel(coroutineScope) {
    private val _idolsLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Loaded<List<IdolColor>>(listOf())) }

    val uiModel: Flow<FullscreenPreviewUiModel>
        get() = _idolsLoadState.map { FullscreenPreviewUiModel(it) }
            .apply { launchIn(viewModelScope) }

    fun fetch(ids: List<String>) {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.search(ids) }
                .onSuccess { _idolsLoadState.value = LoadState.Loaded(it) }
                .onFailure { _idolsLoadState.value = LoadState.Error(it) }
        }

        _idolsLoadState.value = LoadState.Loading
        job.start()
    }
}
