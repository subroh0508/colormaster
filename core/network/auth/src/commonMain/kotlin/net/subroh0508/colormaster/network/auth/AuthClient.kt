package net.subroh0508.colormaster.network.auth

import dev.gitlive.firebase.auth.FirebaseAuth
import net.subroh0508.colormaster.network.auth.model.FirebaseUser

expect class AuthClient(auth: FirebaseAuth) {
    val currentUser: FirebaseUser?

    suspend fun signInAnonymously(): FirebaseUser
    suspend fun signOut()
}
