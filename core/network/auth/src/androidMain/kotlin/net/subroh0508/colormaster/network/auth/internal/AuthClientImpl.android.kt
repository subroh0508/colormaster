package net.subroh0508.colormaster.network.auth.internal

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.auth.model.FirebaseUser
import net.subroh0508.colormaster.network.auth.model.Provider
import dev.gitlive.firebase.auth.FirebaseUser as RawFirebaseUser

internal class AuthClientImpl(private val auth: FirebaseAuth) : AuthClient {
    override val currentUser get() = auth.currentUser?.toDataClass()

    override suspend fun signInAnonymously() {
        auth.signInAnonymously()
    }

    override suspend fun signOut() = auth.signOut()

    override fun subscribeAuthState(): Flow<FirebaseUser?> = auth.authStateChanged.map { it?.toDataClass() }

    override suspend fun signInWithGoogle(idToken: String) {
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
        this@AuthClientImpl.getProviderData(),
    )
}
