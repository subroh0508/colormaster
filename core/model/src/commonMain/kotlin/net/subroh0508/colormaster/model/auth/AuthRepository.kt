package net.subroh0508.colormaster.model.auth

import kotlinx.coroutines.flow.Flow

expect interface AuthRepository {
    fun getCurrentUserStream(): Flow<CurrentUser?>

    suspend fun signOut()
}
