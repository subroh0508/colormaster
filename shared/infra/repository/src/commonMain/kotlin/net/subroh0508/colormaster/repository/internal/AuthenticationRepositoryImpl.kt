package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.authentication.model.FirebaseUser
import net.subroh0508.colormaster.api.authentication.model.Provider
import net.subroh0508.colormaster.model.authentication.CredentialProvider
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.repository.AuthenticationRepository

internal class AuthenticationRepositoryImpl(
    private val client: AuthenticationClient,
) : AuthenticationRepository {
    override suspend fun fetchCurrentUser() = (client.currentUser ?: client.signInAnonymously()).toEntity()

    override suspend fun signInWithGoogle(idToken: String) = client.signInWithGoogle(idToken).toEntity()
    override suspend fun linkWithGoogle(idToken: String) = client.linkWithGoogle(idToken).toEntity()
    override suspend fun unlinkWithGoogle() = client.unlinkWithGoogle().toEntity()

    private fun FirebaseUser.toEntity() = CurrentUser(
        uid,
        providers.mapNotNull { it.toValueObject() },
    )

    private fun Provider.toValueObject() = when (id) {
        Provider.PROVIDER_ANONYMOUS -> CredentialProvider.Anonymous
        Provider.PROVIDER_GOOGLE -> email?.let(CredentialProvider::Google)
        Provider.PROVIDER_TWITTER -> displayName?.let(CredentialProvider::Twitter)
        else -> null
    }
}
