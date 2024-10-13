package net.subroh0508.colormaster.data.internal

import kotlinx.coroutines.flow.map
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.data.toEntity
import net.subroh0508.colormaster.model.auth.AuthRepository

internal class AuthRepositoryImpl(
    private val client: AuthClient,
) : AuthRepository {
    override fun currentUser() = client.subscribeAuthState().map { it?.toEntity() }
    override suspend fun signOut() = client.signOut()

    override suspend fun signInWithGoogle() = client.signInWithGoogle()
    override suspend fun signInWithGoogleForMobile() = client.signInWithGoogleForMobile()
}
