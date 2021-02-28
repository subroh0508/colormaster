package net.subroh0508.colormaster.presentation.home.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.common.ViewModel
import net.subroh0508.colormaster.presentation.home.model.HomeUiModel
import net.subroh0508.colormaster.repository.AuthenticationRepository

abstract class HomeViewModel<out T: HomeUiModel>(
    protected val repository: AuthenticationRepository,
    coroutineScope: CoroutineScope? = null,
) : ViewModel(coroutineScope) {
    protected val currentUserLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Initialize) }

    abstract val uiModel: StateFlow<T>

    fun fetchCurrentUser() {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.fetchCurrentUser() }
                .onSuccess { currentUserLoadState.value = LoadState.Loaded(it) }
                .onFailure { currentUserLoadState.value = LoadState.Error(it) }
        }

        currentUserLoadState.value = LoadState.Loading
        job.start()
    }
}
