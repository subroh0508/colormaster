package net.subroh0508.colormaster.api.firestore

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Transaction

actual class FirestoreClient(val js: FirebaseFirestore) {
    actual fun collection(collectionPath: String) = js.collection(collectionPath)

    actual fun collectionGroup(collectionId: String) = js.collectionGroup(collectionId)

    actual fun document(documentPath: String) = js.document(documentPath)

    actual fun batch() = js.batch()

    actual fun setLoggingEnabled(loggingEnabled: Boolean) =
        js.setLoggingEnabled(loggingEnabled)

    actual suspend fun <T> runTransaction(func: suspend Transaction.() -> T) = js.runTransaction(func)

    actual suspend fun clearPersistence() = js.clearPersistence()

    actual fun useEmulator(host: String, port: Int) = js.useEmulator(host, port)

    actual fun setSettings(persistenceEnabled: Boolean?, sslEnabled: Boolean?, host: String?, cacheSizeBytes: Long?) {
        js.setSettings(
            persistenceEnabled = persistenceEnabled,
            sslEnabled = sslEnabled,
            host = host,
            cacheSizeBytes = cacheSizeBytes,
        )
    }

    actual suspend fun disableNetwork() = js.disableNetwork()
    actual suspend fun enableNetwork() = js.enableNetwork()
}
