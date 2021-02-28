package net.subroh0508.colormaster.repository

import net.subroh0508.colormaster.model.authentication.CurrentUser

interface AuthenticationRepository {
    suspend fun fetchCurrentUser(): CurrentUser?
    suspend fun signInWithGoogle(idToken: String): CurrentUser
    suspend fun linkWithGoogle(idToken: String): CurrentUser
    suspend fun unlinkWithGoogle(): CurrentUser
}
