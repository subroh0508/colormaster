package net.subroh0508.colormaster.presentation.home.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.home.model.AuthenticationMessage
import net.subroh0508.colormaster.repository.AuthenticationRepository

class JsAuthenticationViewModel(
    repository: AuthenticationRepository,
    coroutineScope: CoroutineScope? = null,
) : AuthenticationViewModel(repository, coroutineScope) {
    val signedIn get() = uiModel.value.isSignedIn

    fun subscribe() {
        repository.subscribe().onEach {
            currentUserLoadState.value = LoadState.Loaded(it)
        }.launchIn(viewModelScope)
    }

    fun signInGoogle(isMobile: Boolean) {
        if (isMobile) {
            viewModelScope.launch {
                runCatching { repository.signInWithGoogleForMobile() }
            }

            return
        }

        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.signInWithGoogle() }
                .onSuccess {
                    currentUserLoadState.value = LoadState.Loaded(it)
                    message.value = it.toMessageByGoogle(AuthenticationMessage.Success::LinkAccount)
                }
                .onFailure { message.value = AuthenticationMessage.Failure(it) }
        }

        message.value = AuthenticationMessage.Progress(job)
        job.start()
    }
}
