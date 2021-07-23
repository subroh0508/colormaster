package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.SnapshotMetadata as FirestoreSnapshotMetadata

actual class SnapshotMetadata(val android: FirestoreSnapshotMetadata) {
    actual val hasPendingWrites: Boolean get() = android.hasPendingWrites()
    actual val isFromCache: Boolean get() = android.isFromCache
}
