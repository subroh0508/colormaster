package net.subroh0508.colormaster.features.home.viewmodel

import net.subroh0508.colormaster.model.auth.CurrentUser

sealed interface AuthUiState {
    data object NotSignedIn : AuthUiState
    data class SignedIn(
        val user: CurrentUser,
    ) : AuthUiState
}
