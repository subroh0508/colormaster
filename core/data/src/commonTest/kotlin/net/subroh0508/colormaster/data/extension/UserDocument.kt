package net.subroh0508.colormaster.data.extension

import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient

expect suspend fun setUserDocument(
    auth: AuthClient,
    firestore: FirestoreClient,
    inChargeIds: List<String> = listOf(),
    favoriteIds: List<String> = listOf(),
)
