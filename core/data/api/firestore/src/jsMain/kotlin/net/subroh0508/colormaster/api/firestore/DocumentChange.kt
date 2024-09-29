package net.subroh0508.colormaster.api.firestore

import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual class DocumentChange(val js: firebase.firestore.DocumentChange) {
    actual val document: DocumentSnapshot
        get() = DocumentSnapshot(js.doc)
    actual val newIndex: Int
        get() = js.newIndex
    actual val oldIndex: Int
        get() = js.oldIndex
    actual val type: ChangeType
        get() = ChangeType.values().first { it.jsString == js.type }
}
