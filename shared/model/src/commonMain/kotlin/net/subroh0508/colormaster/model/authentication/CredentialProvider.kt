package net.subroh0508.colormaster.model.authentication

sealed class CredentialProvider {
    object Anonymous : CredentialProvider()
    data class Google(val email: String) : CredentialProvider()
    data class Twitter(val displayName: String) : CredentialProvider()
}
