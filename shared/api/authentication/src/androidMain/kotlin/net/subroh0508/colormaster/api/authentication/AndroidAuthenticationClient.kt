package net.subroh0508.colormaster.api.authentication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import kotlinx.coroutines.tasks.await
import net.subroh0508.colormaster.api.authentication.model.FirebaseUser
import com.google.firebase.auth.FirebaseUser as RawFirebaseUser
import net.subroh0508.colormaster.api.authentication.model.Provider

actual class AuthenticationClient(
    private val auth: FirebaseAuth,
) {
    actual val currentUser get() = auth.currentUser?.toDataClass()

    actual suspend fun signInAnonymously() = auth.signInAnonymously().await().user?.toDataClass() ?: throw IllegalStateException()

    actual suspend fun signOut() = auth.signOut()

    suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return auth.signInWithCredential(credential).await().user?.toDataClass() ?: throw IllegalStateException()
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
