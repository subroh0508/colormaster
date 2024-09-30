package net.subroh0508.colormaster.test.fake

import kotlinx.coroutines.flow.Flow
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.auth.model.FirebaseUser

expect class FakeAuthClient : AuthClient {
    override val currentUser: FirebaseUser?

    override suspend fun signInAnonymously()
    override suspend fun signOut()

    override fun subscribeAuthState(): Flow<FirebaseUser?>
}