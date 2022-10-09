package net.subroh0508.colormaster.api.firestore

import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual class QuerySnapshot(val js: firebase.firestore.QuerySnapshot) {
    actual val documents get() = js.docs.map(::DocumentSnapshot)
    actual val documentChanges get() = js.docChanges().map(::DocumentChange)
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(js.metadata)
}
