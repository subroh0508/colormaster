package net.subroh0508.colormaster.api.firestore

expect object FieldValue {
    val serverTimestamp: Double
    val delete: Any
    fun arrayUnion(vararg elements: Any): Any
    fun arrayRemove(vararg elements: Any): Any
}
