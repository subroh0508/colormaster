package net.subroh0508.colormaster.test.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.auth.model.FirebaseUser
import net.subroh0508.colormaster.test.data.anonymous
import net.subroh0508.colormaster.test.data.fromGoogle

actual class FakeAuthClient : AuthClient {
    private val currentUserStateFlow = MutableStateFlow<FirebaseUser?>(null)

    actual override val currentUser: FirebaseUser? get() = currentUserStateFlow.value

    actual override suspend fun signInAnonymously() {
        currentUserStateFlow.value = anonymous
    }

    actual override suspend fun signOut() {
        currentUserStateFlow.value = null
    }

    actual override fun subscribeAuthState(): Flow<FirebaseUser?> = currentUserStateFlow

    override suspend fun signInWithGoogle() {
        currentUserStateFlow.value = fromGoogle
    }
    override suspend fun signInWithGoogleForMobile() {
        currentUserStateFlow.value = fromGoogle
    }
}