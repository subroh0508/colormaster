package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.Flow
import net.subroh0508.colormaster.model.authentication.CurrentUser

actual interface AuthenticationRepository {
    actual suspend fun signOut()
    fun subscribe(): Flow<CurrentUser?>
    suspend fun signInWithGoogle(): CurrentUser
    suspend fun signInWithGoogleForMobile()
}
