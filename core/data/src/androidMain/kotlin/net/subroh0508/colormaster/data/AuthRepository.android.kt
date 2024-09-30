package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.Flow
import net.subroh0508.colormaster.data.internal.AuthRepositoryImpl
import net.subroh0508.colormaster.model.auth.CurrentUser
import net.subroh0508.colormaster.network.auth.AuthClient

actual interface AuthRepository {
    actual companion object {
        internal actual operator fun invoke(
            client: AuthClient,
        ): AuthRepository = AuthRepositoryImpl(client)
    }

    actual fun currentUser(): Flow<CurrentUser?>
    actual suspend fun signOut()

    suspend fun fetchCurrentUser(): CurrentUser?
    suspend fun signInWithGoogle(idToken: String)
}
