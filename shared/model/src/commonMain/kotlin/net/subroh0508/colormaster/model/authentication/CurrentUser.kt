package net.subroh0508.colormaster.model.authentication

data class CurrentUser(
    val id: String,
    val credentialProviders: List<CredentialProvider>,
) {
    val isAnonymous = credentialProviders.isEmpty() || credentialProviders.find { it is CredentialProvider.Anonymous } != null
}
