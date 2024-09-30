package net.subroh0508.colormaster.data.internal

import kotlinx.coroutines.flow.map
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.data.AuthRepository

internal actual class AuthRepositoryImpl actual constructor(
    private val client: AuthClient,
) : AuthRepository {
    override fun subscribe() = client.subscribeAuthState().map { it?.toEntity() }
    override suspend fun signInWithGoogle() = client.signInWithGoogle().toEntity()
    override suspend fun signInWithGoogleForMobile() = client.signInWithGoogleForMobile()
    override suspend fun signOut() = client.signOut()
}
