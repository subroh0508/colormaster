package net.subroh0508.colormaster.presentation.home.model

import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.common.LoadState

data class AuthenticationUiModel(
    val currentUser: CurrentUser? = null,
    val message: AuthenticationMessage = AuthenticationMessage.Consumed,
) {
    companion object {
        operator fun invoke(
            currentUserLoadState: LoadState,
            message: AuthenticationMessage,
        ) = AuthenticationUiModel(
            currentUserLoadState.getValueOrNull(),
            message,
        )
    }

    val isSignedIn = currentUser != null
}

