package net.subroh0508.colormaster.model.auth

data class CurrentUser(
    val id: String,
    val credentialProviders: List<CredentialProvider>,
) {
    val isAnonymous = credentialProviders.isEmpty() || credentialProviders.find { it is CredentialProvider.Anonymous } != null

    val providerByGoogle = credentialProviders
        .filterIsInstance<CredentialProvider.Google>()
        .firstOrNull()
}
