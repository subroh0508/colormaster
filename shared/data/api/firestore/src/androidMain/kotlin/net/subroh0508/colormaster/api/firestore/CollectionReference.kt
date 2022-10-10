package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.CollectionReference as FirestoreCollectionReference
import kotlinx.serialization.SerializationStrategy
import net.subroh0508.colormaster.api.firestore.serialization.encode

actual class CollectionReference(override val android: FirestoreCollectionReference) : Query(android) {
    actual val path: String get() = android.path

    actual val document: DocumentReference get() = DocumentReference(android.document())

    actual fun document(documentPath: String) = DocumentReference(android.document(documentPath))

    actual suspend inline fun <reified T> add(data: T, encodeDefaults: Boolean) =
        DocumentReference(android.add(encode(data, encodeDefaults)!!).await())
    actual suspend fun <T> add(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean) =
        DocumentReference(android.add(encode(strategy, data, encodeDefaults)!!).await())
}
