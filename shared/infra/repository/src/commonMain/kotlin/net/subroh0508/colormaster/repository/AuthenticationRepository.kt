package net.subroh0508.colormaster.repository

import net.subroh0508.colormaster.model.authentication.CurrentUser

expect interface AuthenticationRepository {
    suspend fun fetchCurrentUser(): CurrentUser?
    suspend fun signOut()
}
