package net.subroh0508.colormaster.api.firestore

expect class FieldPath(vararg fieldNames: String) {
    val documentId: FieldPath
}
