package net.subroh0508.colormaster.network.auth

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.externals.GoogleAuthProvider
import dev.gitlive.firebase.auth.externals.User
import dev.gitlive.firebase.auth.externals.onAuthStateChanged
import dev.gitlive.firebase.auth.externals.signInWithPopup
import dev.gitlive.firebase.auth.externals.signInWithRedirect
import dev.gitlive.firebase.auth.js
import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.subroh0508.colormaster.network.auth.model.FirebaseUser
import net.subroh0508.colormaster.network.auth.model.Provider
import dev.gitlive.firebase.auth.FirebaseUser as RawFirebaseUser

actual class AuthClient actual constructor(
    private val auth: FirebaseAuth,
) {
    actual val currentUser get() = auth.currentUser?.toDataClass()

    actual suspend fun signInAnonymously() {
        auth.signInAnonymously()
    }

    actual suspend fun signOut() = auth.signOut()

    actual fun subscribeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val unsubscribe = onAuthStateChanged(auth.js) { trySend(it?.toDataClass()) }

        awaitClose { unsubscribe() }
    }

    suspend fun signInWithGoogle() {
        signInWithPopup(auth.js, GoogleAuthProvider()).await()
    }
    suspend fun signInWithGoogleForMobile() {
        signInWithRedirect(auth.js, GoogleAuthProvider()).await<Nothing>()
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

    private fun User.toDataClass() = FirebaseUser(
        uid,
        this@AuthClient.getProviderData(),
    )
}
