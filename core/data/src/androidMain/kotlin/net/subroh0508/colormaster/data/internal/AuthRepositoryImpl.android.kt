package net.subroh0508.colormaster.data.internal

import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.data.AuthRepository

internal actual class AuthRepositoryImpl actual constructor(
    private val client: AuthClient,
) : AuthRepository {
    override suspend fun fetchCurrentUser() = client.currentUser?.toEntity()
    override suspend fun signInWithGoogle(idToken: String) = client.signInWithGoogle(idToken).toEntity()
    override suspend fun signOut() = client.signOut()
}
