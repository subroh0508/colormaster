package net.subroh0508.colormaster.repository

expect interface AuthenticationRepository {
    suspend fun signOut()
}
