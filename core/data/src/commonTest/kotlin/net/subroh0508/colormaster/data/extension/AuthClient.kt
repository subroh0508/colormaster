package net.subroh0508.colormaster.data.extension

import net.subroh0508.colormaster.network.auth.AuthClient

expect suspend fun signInWithGoogle(
    client: AuthClient,
)
