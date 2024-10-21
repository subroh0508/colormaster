package net.subroh0508.colormaster.features.home.viewmodel

import net.subroh0508.colormaster.model.auth.AuthRepository

actual class AuthViewModel(
    repository: AuthRepository,
) : CommonAuthViewModel(repository) {
    suspend fun signInWithGoogle(
        isMobile: Boolean,
    ) = if (isMobile)
            repository.signInWithGoogleForMobile()
        else
            repository.signInWithGoogle()
}