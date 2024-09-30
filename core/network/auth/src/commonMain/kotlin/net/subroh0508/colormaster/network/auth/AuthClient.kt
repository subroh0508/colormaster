package net.subroh0508.colormaster.network.auth

import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import net.subroh0508.colormaster.network.auth.model.FirebaseUser

expect interface AuthClient {
    companion object {
        internal operator fun invoke(auth: FirebaseAuth): AuthClient
    }

    val currentUser: FirebaseUser?

    suspend fun signInAnonymously()
    suspend fun signOut()

    fun subscribeAuthState(): Flow<FirebaseUser?>
}
