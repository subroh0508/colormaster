package net.subroh0508.colormaster.api.firestore

actual enum class ChangeType(internal val jsString : String) {
    ADDED("added"),
    MODIFIED("modified"),
    REMOVED("removed")
}
