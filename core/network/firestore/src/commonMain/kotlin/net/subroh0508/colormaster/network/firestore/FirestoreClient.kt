package net.subroh0508.colormaster.network.firestore

import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.FirebaseFirestore
import net.subroh0508.colormaster.network.firestore.document.UserDocument

interface FirestoreClient {
    fun getUsersCollection(): CollectionReference

    suspend fun getUserDocument(uid: String?): UserDocument
}
