package net.subroh0508.colormaster.presentation.home.model

import kotlinx.coroutines.Job
import net.subroh0508.colormaster.model.authentication.CredentialProvider

sealed class AuthenticationMessage {
    object Consumed : AuthenticationMessage()

    data class Progress(val job: Job) : AuthenticationMessage() {
        val cancelled get() = job.isCancelled
    }

    sealed class Success : AuthenticationMessage() {
        data class LinkAccount(val provider: CredentialProvider) : Success()
        data class UnlinkAccount(val provider: CredentialProvider) : Success()
        data class SignIn(val provider: CredentialProvider) : Success()
        data class SignOut(val provider: CredentialProvider) : Success()
    }

    data class Failure(val error: Throwable) : AuthenticationMessage()

    fun cancel() { if (this is Progress) job.cancel() }
}
