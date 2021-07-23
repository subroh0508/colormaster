package net.subroh0508.colormaster.api.firestore

import kotlinx.serialization.DeserializationStrategy
import net.subroh0508.colormaster.api.firestore.serialization.decode
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual class DocumentSnapshot(val js: firebase.firestore.DocumentSnapshot) {
    actual val id get() = js.id
    actual val reference get() = DocumentReference(js.ref)

    actual inline fun <reified T : Any> data(): T = decode(value = js.data())
    actual fun <T> data(strategy: DeserializationStrategy<T>): T = decode(strategy, js.data())
    actual inline fun <reified T> get(field: String) = decode<T>(value = js.get(field))
    actual fun <T> get(field: String, strategy: DeserializationStrategy<T>) = decode(strategy, js.get(field))

    actual fun contains(field: String) = js.get(field) != undefined
    actual val exists get() = js.exists
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(js.metadata)
}
