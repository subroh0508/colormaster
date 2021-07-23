package net.subroh0508.colormaster.api.firestore

import kotlinx.serialization.SerializationStrategy

expect class Transaction {
    fun set(documentRef: DocumentReference, data: Any, encodeDefaults: Boolean = true, merge: Boolean = false): Transaction
    fun set(documentRef: DocumentReference, data: Any, encodeDefaults: Boolean = true, vararg mergeFields: String): Transaction
    fun set(documentRef: DocumentReference, data: Any, encodeDefaults: Boolean = true, vararg mergeFieldPaths: FieldPath): Transaction

    fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, merge: Boolean = false): Transaction
    fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, vararg mergeFields: String): Transaction
    fun <T> set(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, vararg mergeFieldPaths: FieldPath): Transaction

    fun update(documentRef: DocumentReference, data: Any, encodeDefaults: Boolean = true): Transaction
    fun <T> update(documentRef: DocumentReference, strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true): Transaction

    fun update(documentRef: DocumentReference, vararg fieldsAndValues: Pair<String, Any?>): Transaction
    fun update(documentRef: DocumentReference, vararg fieldsAndValues: Pair<FieldPath, Any?>): Transaction

    fun delete(documentRef: DocumentReference): Transaction
    suspend fun get(documentRef: DocumentReference): DocumentSnapshot
}
