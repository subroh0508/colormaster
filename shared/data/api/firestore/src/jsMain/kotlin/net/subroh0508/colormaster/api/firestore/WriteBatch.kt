package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.await
import kotlinx.serialization.SerializationStrategy
import net.subroh0508.colormaster.api.firestore.serialization.encode
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase
import kotlin.js.json

actual class WriteBatch(val js: firebase.firestore.WriteBatch) {
    actual inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean, merge: Boolean) =
        js.set(documentRef.js, encode(data, encodeDefaults)!!, json("merge" to merge)).let { this }

    actual inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean, vararg mergeFields: String) =
        js.set(documentRef.js, encode(data, encodeDefaults)!!, json("mergeFields" to mergeFields)).let { this }

    actual inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean, vararg mergeFieldPaths: FieldPath) =
        js.set(documentRef.js, encode(data, encodeDefaults)!!, json("mergeFields" to mergeFieldPaths.map(FieldPath::js).toTypedArray())).let { this }

    actual fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, merge: Boolean) =
        js.set(documentRef.js, encode(strategy, data, encodeDefaults)!!, json("merge" to merge)).let { this }

    actual fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, vararg mergeFields: String) =
        js.set(documentRef.js, encode(strategy, data, encodeDefaults)!!, json("mergeFields" to mergeFields)).let { this }

    actual fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, vararg mergeFieldPaths: FieldPath) =
        js.set(documentRef.js, encode(strategy, data, encodeDefaults)!!, json("mergeFields" to mergeFieldPaths.map(FieldPath::js).toTypedArray())).let { this }

    actual inline fun <reified T> update(documentRef: DocumentReference, data: T, encodeDefaults: Boolean) =
        js.update(documentRef.js, encode(data, encodeDefaults)!!).let { this }

    actual fun <T> update(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean) =
        js.update(documentRef.js, encode(strategy, data, encodeDefaults)!!).let { this }

    actual fun update(documentRef: DocumentReference, vararg fieldsAndValues: Pair<String, Any?>) =
        js.takeUnless { fieldsAndValues.isEmpty() }
            ?.update(
                documentRef.js,
                fieldsAndValues[0].first,
                fieldsAndValues[0].second,
                *fieldsAndValues.drop(1).flatMap { (field, value) ->
                    listOf(field, value?.let { encode(value, true) })
                }.toTypedArray()
            )
            .let { this }

    actual fun update(documentRef: DocumentReference, vararg fieldsAndValues: Pair<FieldPath, Any?>) =
        js.takeUnless { fieldsAndValues.isEmpty() }
            ?.update(
                documentRef.js,
                fieldsAndValues[0].first.js,
                fieldsAndValues[0].second,
                *fieldsAndValues.flatMap { (field, value) ->
                    listOf(field.js, value?.let { encode(value, true) })
                }.toTypedArray()
            )
            .let { this }

    actual fun delete(documentRef: DocumentReference) = js.delete(documentRef.js).let { this }

    actual suspend fun commit() = js.commit().await()
}
