package net.subroh0508.colormaster.network.auth

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.android
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.subroh0508.colormaster.network.auth.model.FirebaseUser
import dev.gitlive.firebase.auth.FirebaseUser as RawFirebaseUser
import net.subroh0508.colormaster.network.auth.model.Provider

actual class AuthClient actual constructor(
    private val auth: FirebaseAuth,
) {
    actual val currentUser get() = auth.currentUser?.toDataClass()

    actual suspend fun signInAnonymously() {
        auth.signInAnonymously()
    }

    actual suspend fun signOut() = auth.signOut()

    actual fun subscribeAuthState(): Flow<FirebaseUser?> = auth.authStateChanged.map { it?.toDataClass() }

    suspend fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.credential(idToken, null)

        auth.signInWithCredential(credential)
    }

    private fun getProviderData(): List<Provider> {
        val rawUser = auth.currentUser ?: return listOf()
        if (rawUser.isAnonymous) {
            return listOf(Provider(Provider.PROVIDER_ANONYMOUS, null, null))
        }

        return rawUser.providerData.map { Provider(it.providerId, it.email, it.displayName) }
    }

    private fun RawFirebaseUser.toDataClass() = FirebaseUser(
        uid,
        this@AuthClient.getProviderData(),
    )
}
