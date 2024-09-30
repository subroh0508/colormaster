package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.Flow
import net.subroh0508.colormaster.model.auth.CurrentUser

actual interface AuthRepository {
    actual suspend fun signOut()
    fun subscribe(): Flow<CurrentUser?>
    suspend fun signInWithGoogle(): CurrentUser
    suspend fun signInWithGoogleForMobile()
}
