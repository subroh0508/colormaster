package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch as FirestoreWriteBatch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.SerializationStrategy
import net.subroh0508.colormaster.api.firestore.serialization.encode

actual class WriteBatch(val android: FirestoreWriteBatch) {
    actual inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean, merge: Boolean) = when(merge) {
        true -> android.set(documentRef.android, encode(data, encodeDefaults)!!, SetOptions.merge())
        false -> android.set(documentRef.android, encode(data, encodeDefaults)!!)
    }.let { this }

    actual inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean, vararg mergeFields: String) =
        android.set(documentRef.android, encode(data, encodeDefaults)!!, SetOptions.mergeFields(*mergeFields)).let { this }

    actual inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean, vararg mergeFieldPaths: FieldPath) =
        android.set(documentRef.android, encode(data, encodeDefaults)!!, SetOptions.mergeFieldPaths(mergeFieldPaths.map { it.android })).let { this }

    actual fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, merge: Boolean) = when(merge) {
        true -> android.set(documentRef.android, encode(strategy, data, encodeDefaults)!!, SetOptions.merge())
        false -> android.set(documentRef.android, encode(strategy, data, encodeDefaults)!!)
    }.let { this }

    actual fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, vararg mergeFields: String) =
        android.set(documentRef.android, encode(strategy, data, encodeDefaults)!!, SetOptions.mergeFields(*mergeFields)).let { this }

    actual fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, vararg mergeFieldPaths: FieldPath) =
        android.set(documentRef.android, encode(strategy, data, encodeDefaults)!!, SetOptions.mergeFieldPaths(mergeFieldPaths.map { it.android })).let { this }

    @Suppress("UNCHECKED_CAST")
    actual inline fun <reified T> update(documentRef: DocumentReference, data: T, encodeDefaults: Boolean) =
        android.update(documentRef.android, encode(data, encodeDefaults) as Map<String, Any>).let { this }

    @Suppress("UNCHECKED_CAST")
    actual fun <T> update(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean) =
        android.update(documentRef.android, encode(strategy, data, encodeDefaults) as Map<String, Any>).let { this }

    @JvmName("updateFields")
    actual fun update(documentRef: DocumentReference, vararg fieldsAndValues: Pair<String, Any?>) =
        android.takeUnless { fieldsAndValues.isEmpty() }
            ?.update(
                documentRef.android,
                fieldsAndValues[0].first,
                fieldsAndValues[0].second,
                *fieldsAndValues.drop(1).flatMap { (field, value) ->
                    listOf(field, value?.let { encode(value, true) })
                }.toTypedArray()
            ).let { this }

    @JvmName("updateFieldPaths")
    actual fun update(documentRef: DocumentReference, vararg fieldsAndValues: Pair<FieldPath, Any?>) =
        android.takeUnless { fieldsAndValues.isEmpty() }
            ?.update(
                documentRef.android,
                fieldsAndValues[0].first.android,
                fieldsAndValues[0].second,
                *fieldsAndValues.drop(1).flatMap { (field, value) ->
                    listOf(field.android, value?.let { encode(value, true) })
                }.toTypedArray()
            ).let { this }

    actual fun delete(documentRef: DocumentReference) = android.delete(documentRef.android).let { this }

    actual suspend fun commit() { android.commit().await() }
}
