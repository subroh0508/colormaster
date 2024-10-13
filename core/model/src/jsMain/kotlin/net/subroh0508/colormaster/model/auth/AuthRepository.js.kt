package net.subroh0508.colormaster.model.auth

import kotlinx.coroutines.flow.Flow

actual interface AuthRepository {
    actual fun currentUser(): Flow<CurrentUser?>
    actual suspend fun signOut()

    suspend fun signInWithGoogle()
    suspend fun signInWithGoogleForMobile()
}
