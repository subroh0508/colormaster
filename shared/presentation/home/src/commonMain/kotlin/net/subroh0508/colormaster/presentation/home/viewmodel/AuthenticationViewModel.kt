package net.subroh0508.colormaster.presentation.home.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.authentication.CredentialProvider
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.common.ViewModel
import net.subroh0508.colormaster.presentation.home.model.AuthenticationMessage
import net.subroh0508.colormaster.presentation.home.model.AuthenticationUiModel
import net.subroh0508.colormaster.repository.AuthenticationRepository

abstract class AuthenticationViewModel(
    protected val repository: AuthenticationRepository,
    coroutineScope: CoroutineScope? = null,
) : ViewModel(coroutineScope) {
    protected val currentUserLoadState: MutableStateFlow<LoadState> by lazy { MutableStateFlow(LoadState.Initialize) }
    protected val message: MutableStateFlow<AuthenticationMessage> by lazy { MutableStateFlow(AuthenticationMessage.Consumed) }

    val uiModel by lazy {
        combine(currentUserLoadState, message) { currentUserLoadState, message ->
            AuthenticationUiModel(currentUserLoadState, message)
        }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.Eagerly, AuthenticationUiModel())
    }

    fun signOut() {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.signOut() }
                .onSuccess { currentUserLoadState.value = LoadState.Loaded(null) }
                .onFailure { currentUserLoadState.value = LoadState.Error(it) }
        }

        currentUserLoadState.value = LoadState.Loading
        job.start()
    }

    protected fun CurrentUser.toMessageByGoogle(
        constructor: (CredentialProvider.Google) -> AuthenticationMessage.Success,
    ) = providerByGoogle?.let(constructor::invoke) ?: AuthenticationMessage.Failure(IllegalStateException())
}
