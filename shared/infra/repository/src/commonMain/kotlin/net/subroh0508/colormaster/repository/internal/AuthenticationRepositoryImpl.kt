package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.authentication.AuthenticationClient
import net.subroh0508.colormaster.api.authentication.model.FirebaseUser
import net.subroh0508.colormaster.api.authentication.model.Provider
import net.subroh0508.colormaster.model.authentication.CredentialProvider
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.repository.AuthenticationRepository

internal expect class AuthenticationRepositoryImpl(
    client: AuthenticationClient,
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
