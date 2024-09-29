package net.subroh0508.colormaster.network.firestore

import dev.gitlive.firebase.firestore.FirebaseFirestore
import net.subroh0508.colormaster.network.firestore.document.UserDocument

class FirestoreClient(private val firestore: FirebaseFirestore) {
    fun getUsersCollection() = firestore.collection(COLLECTION_USERS)

    suspend fun getUserDocument(uid: String?): UserDocument {
        uid ?: return UserDocument()

        return getUsersCollection().document(uid)
            .get()
            .takeIf { it.exists }
            ?.data(UserDocument.serializer())
            ?: UserDocument()
    }
}
