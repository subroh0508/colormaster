package net.subroh0508.colormaster.data.internal

import net.subroh0508.colormaster.network.authentication.AuthenticationClient
import net.subroh0508.colormaster.data.AuthenticationRepository

internal actual class AuthenticationRepositoryImpl actual constructor(
    private val client: AuthenticationClient,
) : AuthenticationRepository {
    override suspend fun fetchCurrentUser() = client.currentUser?.toEntity()
    override suspend fun signInWithGoogle(idToken: String) = client.signInWithGoogle(idToken).toEntity()
    override suspend fun signOut() = client.signOut()
}