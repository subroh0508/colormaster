package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.SerializationStrategy
import net.subroh0508.colormaster.api.firestore.serialization.encode
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase
import kotlin.js.json

actual class DocumentReference(val js: firebase.firestore.DocumentReference) {
    actual val id: String get() = js.id

    actual val path: String get() = js.path

    actual fun collection(collectionPath: String) = CollectionReference(js.collection(collectionPath))

    actual suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean, merge: Boolean) =
        js.set(encode(data, encodeDefaults)!!, json("merge" to merge)).await()

    actual suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean, vararg mergeFields: String) =
        js.set(encode(data, encodeDefaults)!!, json("mergeFields" to mergeFields)).await()

    actual suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean, vararg mergeFieldPaths: FieldPath) =
        js.set(encode(data, encodeDefaults)!!, json("mergeFields" to mergeFieldPaths.map(FieldPath::js).toTypedArray())).await()

    actual suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, merge: Boolean) =
        js.set(encode(strategy, data, encodeDefaults)!!, json("merge" to merge)).await()

    actual suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, vararg mergeFields: String) =
        js.set(encode(strategy, data, encodeDefaults)!!, json("mergeFields" to mergeFields)).await()

    actual suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean, vararg mergeFieldPaths: FieldPath) =
        js.set(encode(strategy, data, encodeDefaults)!!, json("mergeFields" to mergeFieldPaths.map(FieldPath::js).toTypedArray())).await()

    actual suspend inline fun <reified T> update(data: T, encodeDefaults: Boolean) =
        js.update(encode(data, encodeDefaults)!!).await()

    actual suspend fun <T> update(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean) =
        js.update(encode(strategy, data, encodeDefaults)!!).await()

    actual suspend fun update(vararg fieldsAndValues: Pair<String, Any?>) =
        js.takeUnless { fieldsAndValues.isEmpty() }
            ?.update(
                fieldsAndValues[0].first,
                fieldsAndValues[0].second,
                *fieldsAndValues.drop(1).flatMap { (field, value) ->
                    listOf(field, value?.let { encode(it, true) })
                }.toTypedArray()
            )
            ?.await()
            ?: Unit

    actual suspend fun update(vararg fieldsAndValues: Pair<FieldPath, Any?>) =
        js.takeUnless { fieldsAndValues.isEmpty() }
            ?.update(
                fieldsAndValues[0].first.js,
                fieldsAndValues[0].second,
                *fieldsAndValues.flatMap { (field, value) ->
                    listOf(field.js, value?.let { encode(it, true)!! })
                }.toTypedArray()
            )
            ?.await()
            ?: Unit

    actual suspend fun delete() = js.delete().await()

    actual suspend fun get() = DocumentSnapshot(js.get().await())

    actual val snapshots get() = callbackFlow {
        val unsubscribe = js.onSnapshot(
            { safeOffer(DocumentSnapshot(it)) },
            this::close
        )
        awaitClose { unsubscribe() }
    }
}
