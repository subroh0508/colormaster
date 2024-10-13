package net.subroh0508.colormaster.data

import net.subroh0508.colormaster.model.auth.CredentialProvider
import net.subroh0508.colormaster.model.auth.CurrentUser
import net.subroh0508.colormaster.network.auth.model.FirebaseUser
import net.subroh0508.colormaster.network.auth.model.Provider

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
