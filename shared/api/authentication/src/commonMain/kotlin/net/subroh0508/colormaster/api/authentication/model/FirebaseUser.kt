package net.subroh0508.colormaster.api.authentication.model

data class FirebaseUser(
    val uid: String,
    val providers: List<Provider>,
)
