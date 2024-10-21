package net.subroh0508.colormaster.data.extension

import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.network.firestore.document.UserDocument

actual suspend fun setUserDocument(
    auth: AuthClient,
    firestore: FirestoreClient,
    inChargeIds: List<String>,
    favoriteIds: List<String>,
) {
    auth.signInWithGoogle("idToken")
    val uid = auth.currentUser?.uid ?: return

    firestore.setUserDocument(uid, UserDocument(inChargeIds, favoriteIds))
}
