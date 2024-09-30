package net.subroh0508.colormaster.data

import net.subroh0508.colormaster.model.auth.CurrentUser

actual interface AuthRepository {
    actual suspend fun signOut()
    suspend fun fetchCurrentUser(): CurrentUser?
    suspend fun signInWithGoogle(idToken: String): CurrentUser
}
