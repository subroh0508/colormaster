package net.subroh0508.colormaster.presentation.home.model

import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.common.LoadState

data class AuthenticationUiModel(
    override val currentUser: CurrentUser? = null,
    val message: AuthenticationMessage = AuthenticationMessage.Consumed,
) : HomeUiModel {
    companion object {
        operator fun invoke(
            currentUserLoadState: LoadState,
            message: AuthenticationMessage,
        ) = AuthenticationUiModel(
            currentUserLoadState.getValueOrNull(),
            message,
        )
    }
}

