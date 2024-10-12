package net.subroh0508.colormaster.test.fake

import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.network.firestore.document.UserDocument

class FakeFirestoreClient : FirestoreClient {
    private val store = mutableMapOf<String, UserDocument>()

    override suspend fun setUserDocument(uid: String, userDocument: UserDocument) {
        store[uid] = userDocument
    }

    override suspend fun getUserDocument(uid: String?) = store[uid] ?: UserDocument()
}