package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase
import kotlin.js.json

actual class FirestoreClient(val js: firebase.firestore.Firestore) {
    actual fun collection(collectionPath: String) = CollectionReference(js.collection(collectionPath))

    actual fun collectionGroup(collectionId: String) = Query(js.collectionGroup(collectionId))

    actual fun document(documentPath: String) = DocumentReference(js.doc(documentPath))

    actual fun batch() = WriteBatch(js.batch())

    actual fun setLoggingEnabled(loggingEnabled: Boolean) =
        firebase.firestore.setLogLevel( if(loggingEnabled) "error" else "silent")

    actual suspend fun <T> runTransaction(func: suspend Transaction.() -> T) = js.runTransaction { GlobalScope.promise { Transaction(it).func() } }.await()

    actual suspend fun clearPersistence() = js.clearPersistence().await()

    actual fun useEmulator(host: String, port: Int) = js.useEmulator(host, port)

    actual fun setSettings(persistenceEnabled: Boolean?, sslEnabled: Boolean?, host: String?, cacheSizeBytes: Long?) {
        if(persistenceEnabled == true) js.enablePersistence()

        js.settings(json().apply {
            sslEnabled?.let { set("ssl", it) }
            host?.let { set("host", it) }
            cacheSizeBytes?.let { set("cacheSizeBytes", it) }
        })
    }

    actual suspend fun disableNetwork() { js.disableNetwork().await() }
    actual suspend fun enableNetwork() { js.enableNetwork().await() }
}
