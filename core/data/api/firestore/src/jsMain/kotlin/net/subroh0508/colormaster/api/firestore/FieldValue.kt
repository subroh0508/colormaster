package net.subroh0508.colormaster.api.firestore

import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual object FieldValue {
    actual val serverTimestamp = Double.POSITIVE_INFINITY
    actual val delete: Any get() = firebase.firestore.FieldValue.delete()
    actual fun arrayUnion(vararg elements: Any): Any = firebase.firestore.FieldValue.arrayUnion(*elements)
    actual fun arrayRemove(vararg elements: Any): Any = firebase.firestore.FieldValue.arrayRemove(*elements)
}
