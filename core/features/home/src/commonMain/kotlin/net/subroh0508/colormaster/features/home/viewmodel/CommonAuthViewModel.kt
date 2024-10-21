package net.subroh0508.colormaster.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.subroh0508.colormaster.model.auth.AuthRepository

abstract class CommonAuthViewModel(
    protected val repository: AuthRepository,
) : ViewModel() {
    val uiState: StateFlow<AuthUiState> = repository.getCurrentUserStream()
        .map { user ->
            if (user == null) AuthUiState.NotSignedIn
            else AuthUiState.SignedIn(user)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthUiState.NotSignedIn,
        )

    suspend fun signOut() = repository.signOut()
}