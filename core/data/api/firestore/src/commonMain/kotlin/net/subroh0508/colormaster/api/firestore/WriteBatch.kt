package net.subroh0508.colormaster.api.firestore

import kotlinx.serialization.SerializationStrategy

expect class WriteBatch {
    inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean = true, merge: Boolean = false): WriteBatch
    inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean = true, vararg mergeFields: String): WriteBatch
    inline fun <reified T> set(documentRef: DocumentReference, data: T, encodeDefaults: Boolean = true, vararg mergeFieldPaths: FieldPath): WriteBatch

    fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, merge: Boolean = false): WriteBatch
    fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, vararg mergeFields: String): WriteBatch
    fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, vararg mergeFieldPaths: FieldPath): WriteBatch

    inline fun <reified T> update(documentRef: DocumentReference, data: T, encodeDefaults: Boolean = true): WriteBatch
    fun <T> update(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true): WriteBatch

    fun update(documentRef: DocumentReference, vararg fieldsAndValues: Pair<String, Any?>): WriteBatch
    fun update(documentRef: DocumentReference, vararg fieldsAndValues: Pair<FieldPath, Any?>): WriteBatch

    fun delete(documentRef: DocumentReference): WriteBatch
    suspend fun commit()
}
