package net.subroh0508.colormaster.repository

import net.subroh0508.colormaster.model.authentication.CurrentUser

actual interface AuthenticationRepository {
    actual suspend fun signOut()
    suspend fun fetchCurrentUser(): CurrentUser?
    suspend fun signInWithGoogle(idToken: String): CurrentUser
}
