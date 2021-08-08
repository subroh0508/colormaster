package net.subroh0508.colormaster.api.firestore

import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual class SnapshotMetadata(val js: firebase.firestore.SnapshotMetadata) {
    actual val hasPendingWrites: Boolean get() = js.hasPendingWrites
    actual val isFromCache: Boolean get() = js.fromCache
}
