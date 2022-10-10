package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.DocumentChange as FirestoreDocumentChange

actual class DocumentChange(val android: FirestoreDocumentChange) {
    actual val document: DocumentSnapshot get() = DocumentSnapshot(android.document)
    actual val newIndex: Int get() = android.newIndex
    actual val oldIndex: Int get() = android.oldIndex
    actual val type: ChangeType get() = android.type
}
