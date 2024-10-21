package net.subroh0508.colormaster.features.home.viewmodel

import net.subroh0508.colormaster.model.auth.AuthRepository

actual class AuthViewModel(
    repository: AuthRepository,
) : CommonAuthViewModel(repository) {
    suspend fun signInWithGoogle(idToken: String) = repository.signInWithGoogle(idToken)
}