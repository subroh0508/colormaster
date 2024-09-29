package net.subroh0508.colormaster.api.firestore

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction

actual class FirestoreClient(val android: FirebaseFirestore) {
    actual fun collection(collectionPath: String) = android.collection(collectionPath)

    actual fun collectionGroup(collectionId: String) = android.collectionGroup(collectionId)

    actual fun document(documentPath: String) = android.document(documentPath)

    actual fun batch() = android.batch()

    actual fun setLoggingEnabled(loggingEnabled: Boolean) = android.setLoggingEnabled(loggingEnabled)

    actual suspend fun <T> runTransaction(func: suspend Transaction.() -> T): T =
        android.runTransaction(func)

    actual suspend fun clearPersistence() =
        android.clearPersistence()

    actual fun useEmulator(host: String, port: Int) =
        android.useEmulator(host, port)

    actual fun setSettings(persistenceEnabled: Boolean?, sslEnabled: Boolean?, host: String?, cacheSizeBytes: Long?) {
        android.setSettings(
            persistenceEnabled = persistenceEnabled,
            sslEnabled = sslEnabled,
            host = host,
            cacheSizeBytes = cacheSizeBytes
        )
    }

    actual suspend fun disableNetwork() =
        android.disableNetwork()

    actual suspend fun enableNetwork() =
        android.enableNetwork()
}
