package net.subroh0508.colormaster.network.authentication.model

data class Provider(
    val id: String,
    val email: String?,
    val displayName: String?,
) {
    companion object {
        const val PROVIDER_ANONYMOUS = "anonymous"
        const val PROVIDER_GOOGLE = "google.com"
        const val PROVIDER_TWITTER = "twitter.com"
    }
}
