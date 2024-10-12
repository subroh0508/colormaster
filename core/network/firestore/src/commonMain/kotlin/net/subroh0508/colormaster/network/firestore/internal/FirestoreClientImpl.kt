package net.subroh0508.colormaster.network.firestore.internal

import dev.gitlive.firebase.firestore.FirebaseFirestore
import net.subroh0508.colormaster.network.firestore.COLLECTION_USERS
import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.network.firestore.document.UserDocument

internal class FirestoreClientImpl(
    private val firestore: FirebaseFirestore,
) : FirestoreClient {
    override fun getUsersCollection() = firestore.collection(COLLECTION_USERS)

    override suspend fun getUserDocument(uid: String?): UserDocument {
        uid ?: return UserDocument()

        return getUsersCollection().document(uid)
            .get()
            .takeIf { it.exists }
            ?.data(UserDocument.serializer())
            ?: UserDocument()
    }
}
