package net.subroh0508.colormaster.data.internal

import kotlinx.coroutines.flow.map
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.data.AuthRepository
import net.subroh0508.colormaster.data.toEntity

internal class AuthRepositoryImpl(
    private val client: AuthClient,
) : AuthRepository {
    override fun currentUser() = client.subscribeAuthState().map { it?.toEntity() }
    override suspend fun signOut() = client.signOut()

    override suspend fun signInWithGoogle() = client.signInWithGoogle()
    override suspend fun signInWithGoogleForMobile() = client.signInWithGoogleForMobile()
}
