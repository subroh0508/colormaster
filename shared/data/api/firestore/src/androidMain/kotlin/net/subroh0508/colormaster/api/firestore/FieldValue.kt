package net.subroh0508.colormaster.api.firestore

import com.google.firebase.firestore.FieldValue

actual object FieldValue {
    actual val serverTimestamp = Double.POSITIVE_INFINITY
    actual val delete: Any get() = FieldValue.delete()
    actual fun arrayUnion(vararg elements: Any): Any = FieldValue.arrayUnion(*elements)
    actual fun arrayRemove(vararg elements: Any): Any = FieldValue.arrayRemove(*elements)
}
