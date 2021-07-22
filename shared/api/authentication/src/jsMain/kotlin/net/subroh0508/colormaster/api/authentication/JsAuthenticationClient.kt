package net.subroh0508.colormaster.api.authentication

import kotlinx.coroutines.await
import net.subroh0508.colormaster.api.authentication.model.FirebaseUser
import net.subroh0508.colormaster.api.authentication.model.Provider
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual class AuthenticationClient(
    private val auth: firebase.auth.Auth,
) {
    actual val currentUser get() = auth.currentUser?.toDataClass()

    actual suspend fun signInAnonymously() = auth.signInAnonymously().await().user?.toDataClass() ?: throw IllegalStateException()

    actual suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = firebase.auth.GoogleAuthProvider.credential(idToken, null)

        return auth.signInWithCredential(credential).await().user?.toDataClass() ?: throw IllegalStateException()
    }

    actual suspend fun linkWithGoogle(idToken: String): FirebaseUser {
        val rawUser = auth.currentUser ?: throw IllegalStateException()
        if (rawUser.providerData.map(firebase.user.UserInfo::providerId).contains(Provider.PROVIDER_GOOGLE)) {
            return rawUser.toDataClass()
        }

        val credential = firebase.auth.GoogleAuthProvider.credential(idToken, null)
        return rawUser.linkWithCredential(credential).await().user?.toDataClass() ?: throw IllegalStateException()
    }

    actual suspend fun unlinkWithGoogle() =
        auth.currentUser?.unlink(Provider.PROVIDER_GOOGLE)?.await()?.toDataClass() ?: throw IllegalStateException()

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
