package net.subroh0508.colormaster.api.authentication

import net.subroh0508.colormaster.api.authentication.model.FirebaseUser

expect class AuthenticationClient {
    val currentUser: FirebaseUser?

    suspend fun signInAnonymously(): FirebaseUser
    suspend fun signInWithGoogle(idToken: String): FirebaseUser
    suspend fun linkWithGoogle(idToken: String): FirebaseUser
    suspend fun unlinkWithGoogle(): FirebaseUser
}
