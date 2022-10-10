package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.repository.AuthenticationRepository

internal actual class AuthenticationRepositoryImpl actual constructor(
    private val client: AuthenticationClient,
) : AuthenticationRepository {
    override suspend fun fetchCurrentUser() = client.currentUser?.toEntity()
    override suspend fun signInWithGoogle(idToken: String) = client.signInWithGoogle(idToken).toEntity()
    override suspend fun signOut() = client.signOut()
}
