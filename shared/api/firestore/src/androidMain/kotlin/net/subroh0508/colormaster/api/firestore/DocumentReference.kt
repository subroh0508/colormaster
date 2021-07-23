package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import com.google.firebase.firestore.DocumentReference as FirestoreDocumentReference
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.SerializationStrategy
import net.subroh0508.colormaster.api.firestore.serialization.encode

actual class DocumentReference(val android: FirestoreDocumentReference) {
    actual val id: String get() = android.id
    actual val path: String get() = android.path

    actual fun collection(collectionPath: String) = CollectionReference(android.collection(collectionPath))

    actual suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean, merge: Boolean) = when(merge) {
        true -> android.set(encode(data, encodeDefaults)!!, SetOptions.merge())
        false -> android.set(encode(data, encodeDefaults)!!)
    }.await().run { }

    actual suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean, vararg mergeFields: String) =
        android.set(encode(data, encodeDefaults)!!, SetOptions.mergeFields(*mergeFields)).await().run { }

    actual suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean, vararg mergeFieldPaths: FieldPath) =
        android.set(encode(data, encodeDefaults)!!, SetOptions.mergeFieldPaths(mergeFieldPaths.map(FieldPath::android))).await().run { }

    actual suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, merge: Boolean) = when(merge) {
        true -> android.set(encode(strategy, data, encodeDefaults)!!, SetOptions.merge())
        false -> android.set(encode(strategy, data, encodeDefaults)!!)
    }.await().run { }

    actual suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, vararg mergeFields: String) =
        android.set(encode(strategy, data, encodeDefaults)!!, SetOptions.mergeFields(*mergeFields)).await().run { }

    actual suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, vararg mergeFieldPaths: FieldPath) =
        android.set(encode(strategy, data, encodeDefaults)!!, SetOptions.mergeFieldPaths(mergeFieldPaths.map(FieldPath::android))).await().run { }

    @Suppress("UNCHECKED_CAST")
    actual suspend inline fun <reified T> update(data: T, encodeDefaults: Boolean) =
        android.update(encode(data, encodeDefaults) as Map<String, Any>).await().run { }

    @Suppress("UNCHECKED_CAST")
    actual suspend fun <T> update(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean) =
        android.update(encode(strategy, data, encodeDefaults) as Map<String, Any>).await().run { }

    @JvmName("updateFields")
    actual suspend fun update(vararg fieldsAndValues: Pair<String, Any?>) =
        android.takeUnless { fieldsAndValues.isEmpty() }
            ?.update(
                fieldsAndValues[0].first,
                fieldsAndValues[0].second,
                *fieldsAndValues.drop(1).flatMap { (field, value) ->
                    listOf(field, value?.let { encode(value, true) })
                }.toTypedArray()
            )
            ?.await()
            .run { }

    @JvmName("updateFieldPaths")
    actual suspend fun update(vararg fieldsAndValues: Pair<FieldPath, Any?>) =
        android.takeUnless { fieldsAndValues.isEmpty() }
            ?.update(
                fieldsAndValues[0].first.android,
                fieldsAndValues[0].second,
                *fieldsAndValues.drop(1).flatMap { (field, value) ->
                    listOf(field.android, value?.let { encode(value, true) })
                }.toTypedArray()
            )
            ?.await()
            .run { }

    actual suspend fun delete() { android.delete().await() }
    actual suspend fun get() = DocumentSnapshot(android.get().await())

    actual val snapshots get() = callbackFlow {
        val listener = android.addSnapshotListener { snapshot, exception ->
            snapshot?.let { safeOffer(DocumentSnapshot(snapshot)) }
            exception?.let { close(exception) }
        }
        awaitClose(listener::remove)
    }
}
