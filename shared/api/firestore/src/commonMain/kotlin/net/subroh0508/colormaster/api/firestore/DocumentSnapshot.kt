package net.subroh0508.colormaster.api.firestore

import kotlinx.serialization.DeserializationStrategy

expect class DocumentSnapshot {
    inline fun <reified T> get(field: String): T
    fun <T> get(field: String, strategy: DeserializationStrategy<T>): T

    fun contains(field: String): Boolean

    inline fun <reified T: Any> data(): T
    fun <T> data(strategy: DeserializationStrategy<T>): T

    val exists: Boolean
    val id: String
    val reference: DocumentReference
    val metadata: SnapshotMetadata
}
