package net.subroh0508.colormaster.network.authentication

import dev.gitlive.firebase.auth.FirebaseAuth
import net.subroh0508.colormaster.network.authentication.model.FirebaseUser

expect class AuthenticationClient(auth: FirebaseAuth) {
    val currentUser: FirebaseUser?

    suspend fun signInAnonymously(): FirebaseUser
    suspend fun signOut()
}
