package net.subroh0508.colormaster.api.authentication

import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.subroh0508.colormaster.api.authentication.model.FirebaseUser
import net.subroh0508.colormaster.api.authentication.model.Provider
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual class AuthenticationClient(
    private val auth: firebase.auth.Auth,
) {
    actual val currentUser get() = auth.currentUser?.toDataClass()

    actual suspend fun signInAnonymously() = auth.signInAnonymously().await().user?.toDataClass() ?: throw IllegalStateException()

    actual suspend fun signOut() = auth.signOut().await()

    fun subscribeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val unsubscribe = auth.onAuthStateChanged { trySend(it?.toDataClass()) }

        awaitClose { unsubscribe() }
    }

    suspend fun signInWithGoogle() = auth.signInWithPopup(firebase.auth.GoogleAuthProvider()).await().user?.toDataClass() ?: throw IllegalStateException()

    private fun getProviderData(): List<Provider> {
        val rawUser = auth.currentUser ?: return listOf()
        if (rawUser.isAnonymous) {
            return listOf(Provider(Provider.PROVIDER_ANONYMOUS, null, null))
        }

        return rawUser.providerData.map { Provider(it.providerId, it.email, it.displayName) }
    }

    private fun firebase.user.User.toDataClass() = FirebaseUser(
        uid,
        this@AuthenticationClient.getProviderData(),
    )
}
