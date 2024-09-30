package net.subroh0508.colormaster.data.internal

import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.auth.model.FirebaseUser
import net.subroh0508.colormaster.network.auth.model.Provider
import net.subroh0508.colormaster.model.authentication.CredentialProvider
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.data.AuthenticationRepository

internal expect class AuthenticationRepositoryImpl(
    client: AuthClient,
): AuthenticationRepository

internal fun FirebaseUser.toEntity() = CurrentUser(
    uid,
    providers.mapNotNull(Provider::toValueObject),
)

private fun Provider.toValueObject() = when (id) {
    Provider.PROVIDER_ANONYMOUS -> CredentialProvider.Anonymous
    Provider.PROVIDER_GOOGLE -> email?.let(CredentialProvider::Google)
    Provider.PROVIDER_TWITTER -> displayName?.let(CredentialProvider::Twitter)
    else -> null
}
