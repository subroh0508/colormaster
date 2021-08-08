package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.await
import kotlinx.serialization.SerializationStrategy
import net.subroh0508.colormaster.api.firestore.serialization.encode
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual class CollectionReference(override val js: firebase.firestore.CollectionReference) : Query(js) {

    actual val path: String get() =  js.path

    actual val document get() = DocumentReference(js.doc())

    actual fun document(documentPath: String) = DocumentReference(js.doc(documentPath))

    actual suspend inline fun <reified T> add(data: T, encodeDefaults: Boolean) =
        DocumentReference(js.add(encode(data, encodeDefaults)!!).await())

    actual suspend fun <T> add(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean) =
        DocumentReference(js.add(encode(strategy, data, encodeDefaults)!!).await())
}
