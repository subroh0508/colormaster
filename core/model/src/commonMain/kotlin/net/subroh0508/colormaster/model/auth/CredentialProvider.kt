package net.subroh0508.colormaster.model.auth

sealed class CredentialProvider {
    data object Anonymous : CredentialProvider()
    data class Google(val email: String) : CredentialProvider()
    data class Twitter(val displayName: String) : CredentialProvider()
}
