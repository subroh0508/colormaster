package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.FieldPath as FirestoreFieldPath

actual class FieldPath private constructor(val android: FirestoreFieldPath) {
    actual constructor(vararg fieldNames: String) : this(FirestoreFieldPath.of(*fieldNames))
    actual val documentId: FieldPath get() = FieldPath(FirestoreFieldPath.documentId())
}
