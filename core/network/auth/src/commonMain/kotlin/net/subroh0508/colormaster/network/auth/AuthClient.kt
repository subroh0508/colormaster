package net.subroh0508.colormaster.network.auth

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import net.subroh0508.colormaster.network.auth.model.FirebaseUser

expect class AuthClient(auth: FirebaseAuth) {
    val currentUser: FirebaseUser?

    suspend fun signInAnonymously()
    suspend fun signOut()

    fun subscribeAuthState(): Flow<FirebaseUser?>
}
