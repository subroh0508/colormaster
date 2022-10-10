package net.subroh0508.colormaster.api.firestore

import kotlinx.serialization.SerializationStrategy

expect class CollectionReference : Query {
    val path: String
    val document: DocumentReference

    fun document(documentPath: String): DocumentReference
    suspend inline fun <reified T> add(data: T, encodeDefaults: Boolean = true): DocumentReference
    suspend fun <T> add(strategy: SerializationStrategy<T>, data: T, encodeDefaults: Boolean = true): DocumentReference
}
