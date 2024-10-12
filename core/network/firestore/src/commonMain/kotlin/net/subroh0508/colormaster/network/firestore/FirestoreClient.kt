package net.subroh0508.colormaster.network.firestore

import net.subroh0508.colormaster.network.firestore.document.UserDocument

interface FirestoreClient {
    suspend fun setUserDocument(uid: String, userDocument: UserDocument)
    suspend fun getUserDocument(uid: String?): UserDocument
}
