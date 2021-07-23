package net.subroh0508.colormaster.presentation.home.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.home.model.AuthenticationMessage
import net.subroh0508.colormaster.repository.AuthenticationRepository

class AndroidAuthenticationViewModel(
    repository: AuthenticationRepository,
    coroutineScope: CoroutineScope? = null,
) : AuthenticationViewModel(repository, coroutineScope) {
    fun signInWithGoogle(idToken: String) {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching {
                repository.signInWithGoogle(idToken)
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
}
