package net.subroh0508.colormaster.api.firestore

expect class DocumentChange {
    val document: DocumentSnapshot
    val newIndex: Int
    val oldIndex: Int
    val type: ChangeType
}
