package net.subroh0508.colormaster.api.authentication

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import net.subroh0508.colormaster.api.authentication.model.FirebaseUser
import dev.gitlive.firebase.auth.FirebaseUser as RawFirebaseUser
import net.subroh0508.colormaster.api.authentication.model.Provider

actual class AuthenticationClient actual constructor(
    private val auth: FirebaseAuth,
) {
    actual val currentUser get() = auth.currentUser?.toDataClass()

    actual suspend fun signInAnonymously() = auth.signInAnonymously().user?.toDataClass() ?: throw IllegalStateException()

    actual suspend fun signOut() = auth.signOut()

    suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.credential(idToken, null)

        return auth.signInWithCredential(credential).user?.toDataClass() ?: throw IllegalStateException()
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
        this@AuthenticationClient.getProviderData(),
    )
}
