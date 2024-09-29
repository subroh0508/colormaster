package net.subroh0508.colormaster.api.firestore

expect class QuerySnapshot {
    val documents: List<DocumentSnapshot>
    val documentChanges: List<DocumentChange>
    val metadata: SnapshotMetadata
}
