package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

actual class FirestoreClient(val android: FirebaseFirestore) {
    actual fun collection(collectionPath: String) = CollectionReference(android.collection(collectionPath))

    actual fun collectionGroup(collectionId: String) = Query(android.collectionGroup(collectionId))

    actual fun document(documentPath: String) = DocumentReference(android.document(documentPath))

    actual fun batch() = WriteBatch(android.batch())

    actual fun setLoggingEnabled(loggingEnabled: Boolean) = FirebaseFirestore.setLoggingEnabled(loggingEnabled)

    actual suspend fun <T> runTransaction(func: suspend Transaction.() -> T): T =
        android.runTransaction { runBlocking { Transaction(it).func() } }.await()

    actual suspend fun clearPersistence() =
        android.clearPersistence().await().run { }

    actual fun useEmulator(host: String, port: Int) {
        android.useEmulator(host, port)
        android.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
    }

    actual fun setSettings(persistenceEnabled: Boolean?, sslEnabled: Boolean?, host: String?, cacheSizeBytes: Long?) {
        android.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder().also { builder ->
            persistenceEnabled?.let { builder.setPersistenceEnabled(it) }
            sslEnabled?.let { builder.isSslEnabled = it }
            host?.let { builder.host = it }
            cacheSizeBytes?.let { builder.cacheSizeBytes = it }
        }.build()
    }

    actual suspend fun disableNetwork() =
        android.disableNetwork().await().run { }

    actual suspend fun enableNetwork() =
        android.enableNetwork().await().run { }
}
