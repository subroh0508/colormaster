package net.subroh0508.colormaster.data.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import net.subroh0508.colormaster.data.data.anonymous
import net.subroh0508.colormaster.data.data.fromGoogle
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.auth.model.FirebaseUser

class FakeAuthClient : AuthClient {
    private val currentUserStateFlow = MutableStateFlow<FirebaseUser?>(null)

    override val currentUser: FirebaseUser? get() = currentUserStateFlow.value

    override suspend fun signInAnonymously() {
        currentUserStateFlow.value = anonymous
    }

    override suspend fun signOut() {
        currentUserStateFlow.value = null
    }

    override fun subscribeAuthState(): Flow<FirebaseUser?> = currentUserStateFlow

    override suspend fun signInWithGoogle() {
        currentUserStateFlow.value = fromGoogle
    }
    override suspend fun signInWithGoogleForMobile() {
        currentUserStateFlow.value = fromGoogle
    }
}