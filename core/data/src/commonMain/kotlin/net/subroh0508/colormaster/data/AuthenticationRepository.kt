package net.subroh0508.colormaster.data

expect interface AuthenticationRepository {
    suspend fun signOut()
}
