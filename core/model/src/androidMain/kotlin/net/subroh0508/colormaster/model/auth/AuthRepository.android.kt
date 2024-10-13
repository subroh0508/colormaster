package net.subroh0508.colormaster.model.auth

import kotlinx.coroutines.flow.Flow

actual interface AuthRepository {
    actual fun getCurrentUserStream(): Flow<CurrentUser?>
    actual suspend fun signOut()

    suspend fun fetchCurrentUser(): CurrentUser?
    suspend fun signInWithGoogle(idToken: String)
}
