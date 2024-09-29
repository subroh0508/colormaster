package net.subroh0508.colormaster.api.authentication

import dev.gitlive.firebase.auth.FirebaseAuth
import net.subroh0508.colormaster.api.authentication.model.FirebaseUser

expect class AuthenticationClient(auth: FirebaseAuth) {
    val currentUser: FirebaseUser?

    suspend fun signInAnonymously(): FirebaseUser
    suspend fun signOut()
}
