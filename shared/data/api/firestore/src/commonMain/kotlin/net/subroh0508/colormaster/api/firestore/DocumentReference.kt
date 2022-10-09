package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerializationStrategy

expect class DocumentReference {
    val id: String
    val path: String
    val snapshots: Flow<DocumentSnapshot>

    fun collection(collectionPath: String): CollectionReference
    suspend fun get(): DocumentSnapshot

    suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean = true, merge: Boolean = false)
    suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean = true, vararg mergeFields: String)
    suspend inline fun <reified T> set(data: T, encodeDefaults: Boolean = true, vararg mergeFieldPaths: FieldPath)

    suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, merge: Boolean = false)
    suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, vararg mergeFields: String)
    suspend fun <T> set(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true, vararg mergeFieldPaths: FieldPath)

    suspend inline fun <reified T> update(data: T, encodeDefaults: Boolean = true)
    suspend fun <T> update(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true)

    suspend fun update(vararg fieldsAndValues: Pair<String, Any?>)
    suspend fun update(vararg fieldsAndValues: Pair<FieldPath, Any?>)

    suspend fun delete()
}
