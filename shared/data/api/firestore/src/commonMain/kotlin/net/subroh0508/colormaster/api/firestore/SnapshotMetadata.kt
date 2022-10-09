package net.subroh0508.colormaster.api.firestore

expect class SnapshotMetadata {
    val hasPendingWrites: Boolean
    val isFromCache: Boolean
}
