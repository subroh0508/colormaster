package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.QuerySnapshot as FirestoreQuerySnapshot

actual class QuerySnapshot(val android: FirestoreQuerySnapshot) {
    actual val documents get() = android.documents.map(::DocumentSnapshot)
    actual val documentChanges get() = android.documentChanges.map(::DocumentChange)
    actual val metadata: SnapshotMetadata get() = SnapshotMetadata(android.metadata)
}
