package net.subroh0508.colormaster.api.firestore

import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

actual class FieldPath private constructor(val js: firebase.firestore.FieldPath) {
    actual constructor(vararg fieldNames: String) : this(
        js("Reflect").construct(firebase.firestore.FieldPath, fieldNames).unsafeCast<firebase.firestore.FieldPath>()
    )
    actual val documentId: FieldPath get() = FieldPath(firebase.firestore.FieldPath.documentId)
}

