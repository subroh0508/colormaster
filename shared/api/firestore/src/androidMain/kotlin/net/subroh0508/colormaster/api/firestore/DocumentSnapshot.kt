package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.DocumentSnapshot as FirestoreDocumentSnapshot
import kotlinx.serialization.DeserializationStrategy
import net.subroh0508.colormaster.api.firestore.serialization.decode

@Suppress("UNCHECKED_CAST")
actual class DocumentSnapshot(val android: FirestoreDocumentSnapshot) {

    actual val id get() = android.id
    actual val reference get() = DocumentReference(android.reference)

    actual inline fun <reified T: Any> data() = decode<T>(value = android.data)

    actual fun <T> data(strategy: DeserializationStrategy<T>) = decode(strategy, android.data)

    actual inline fun <reified T> get(field: String) = decode<T>(value = android.get(field))

    actual fun <T> get(field: String, strategy: DeserializationStrategy<T>) = decode(strategy, android.get(field))

    actual fun contains(field: String) = android.contains(field)

    actual val exists get() = android.exists()

    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(android.metadata)
}
