package net.subroh0508.colormaster.api.authentication

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
import net.subroh0508.colormaster.api.authentication.model.FirebaseUser
import net.subroh0508.colormaster.api.authentication.model.Provider
import dev.gitlive.firebase.auth.FirebaseUser as RawFirebaseUser

actual class AuthenticationClient actual constructor(
    private val auth: FirebaseAuth,
) {
    actual val currentUser get() = auth.currentUser?.toDataClass()

    actual suspend fun signInAnonymously() = auth.signInAnonymously().user?.toDataClass() ?: throw IllegalStateException()

    actual suspend fun signOut() = auth.signOut()

    fun subscribeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val unsubscribe = onAuthStateChanged(auth.js) { trySend(it?.toDataClass()) }

        awaitClose { unsubscribe() }
    }

    suspend fun signInWithGoogle() = signInWithPopup(auth.js, GoogleAuthProvider()).await().user.toDataClass()
    suspend fun signInWithGoogleForMobile(): Nothing = signInWithRedirect(auth.js, GoogleAuthProvider()).await()

    private fun getProviderData(): List<Provider> {
        val rawUser = auth.currentUser ?: return listOf()
        if (rawUser.isAnonymous) {
            return listOf(Provider(Provider.PROVIDER_ANONYMOUS, null, null))
        }

        return rawUser.providerData.map { Provider(it.providerId, it.email, it.displayName) }
    }

    private fun RawFirebaseUser.toDataClass() = FirebaseUser(
        uid,
        this@AuthenticationClient.getProviderData(),
    )

    private fun User.toDataClass() = FirebaseUser(
        uid,
        this@AuthenticationClient.getProviderData(),
    )
}
