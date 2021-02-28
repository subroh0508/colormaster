package net.subroh0508.colormaster.presentation.home.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.authentication.CredentialProvider
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.home.model.AuthenticationMessage
import net.subroh0508.colormaster.presentation.home.model.AuthenticationUiModel
import net.subroh0508.colormaster.repository.AuthenticationRepository

class AuthenticationViewModel(
    repository: AuthenticationRepository,
    coroutineScope: CoroutineScope? = null,
) : HomeViewModel<AuthenticationUiModel>(repository, coroutineScope) {
    private val message: MutableStateFlow<AuthenticationMessage> by lazy { MutableStateFlow(AuthenticationMessage.Consumed) }

    override val uiModel by lazy {
        combine(currentUserLoadState, message) { currentUserLoadState, message ->
            AuthenticationUiModel(currentUserLoadState, message)
        }.distinctUntilChanged().stateIn(viewModelScope, SharingStarted.Eagerly, AuthenticationUiModel())
    }

    fun signInWithGoogle(idToken: String) {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching {
                repository.signInWithGoogle(idToken)
            }
                .onSuccess {
                    currentUserLoadState.value = LoadState.Loaded(it)
                    message.value = it.toMessageByGoogle(AuthenticationMessage.Success::SignIn)
                }
                .onFailure { message.value = AuthenticationMessage.Failure(it) }
        }

        message.value = AuthenticationMessage.Progress(job)
        job.start()
    }

    fun linkWithGoogle(idToken: String) {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching {
                repository.linkWithGoogle(idToken)
            }
                .onSuccess {
                    currentUserLoadState.value = LoadState.Loaded(it)
                    message.value = it.toMessageByGoogle(AuthenticationMessage.Success::LinkAccount)
                }
                .onFailure { message.value = AuthenticationMessage.Failure(it) }
        }

        message.value = AuthenticationMessage.Progress(job)
        job.start()
    }

    fun unlinkWithGoogle() {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching {
                repository.unlinkWithGoogle()
            }
                .onSuccess {
                    currentUserLoadState.value = LoadState.Loaded(it)
                    message.value = it.toMessageByGoogle(AuthenticationMessage.Success::UnlinkAccount)
                }
                .onFailure { message.value = AuthenticationMessage.Failure(it) }
        }

        message.value = AuthenticationMessage.Progress(job)
        job.start()
    }

    private fun CurrentUser.toMessageByGoogle(
        constructor: (CredentialProvider.Google) -> AuthenticationMessage.Success,
    ) = providerByGoogle?.let(constructor::invoke) ?: AuthenticationMessage.Failure(IllegalStateException())
}
