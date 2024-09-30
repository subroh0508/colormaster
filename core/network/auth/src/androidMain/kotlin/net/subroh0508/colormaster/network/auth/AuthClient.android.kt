package net.subroh0508.colormaster.network.auth

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import net.subroh0508.colormaster.network.auth.internal.AuthClientImpl
import net.subroh0508.colormaster.network.auth.model.FirebaseUser

actual interface AuthClient {
    actual companion object {
        internal actual operator fun invoke(
            auth: FirebaseAuth,
        ): AuthClient = AuthClientImpl(auth)
    }

    actual val currentUser: FirebaseUser?

    actual suspend fun signInAnonymously()

    actual suspend fun signOut()

    actual fun subscribeAuthState(): Flow<FirebaseUser?>

    suspend fun signInWithGoogle(idToken: String)
}
