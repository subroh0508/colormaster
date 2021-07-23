package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import net.subroh0508.colormaster.api.jsfirebaseapp.firebase

@Suppress("FunctionName")
actual open class Query(open val js: firebase.firestore.Query) {
    actual suspend fun get() = QuerySnapshot(js.get().await())

    actual fun limit(limit: Number) = Query(js.limit(limit.toDouble()))

    internal actual fun _where(field: String, equalTo: Any?) = Query(js.where(field, "==", equalTo))
    internal actual fun _where(path: FieldPath, equalTo: Any?) = Query(js.where(path.js, "==", equalTo))
    internal actual fun _where(field: String, equalTo: DocumentReference) = Query(js.where(field, "==", equalTo.js))
    internal actual fun _where(path: FieldPath, equalTo: DocumentReference) = Query(js.where(path.js, "==", equalTo.js))

    internal actual fun _where(
        field: String,
        lessThan: Any?,
        greaterThan: Any?,
        arrayContains: Any?
    ) = Query(
        (lessThan?.let { js.where(field, "<", it) } ?: js).let { js2 ->
            (greaterThan?.let { js2.where(field, ">", it) } ?: js2).let { js3 ->
                arrayContains?.let { js3.where(field, "array-contains", it) } ?: js3
            }
        }
    )

    internal actual fun _where(
        path: FieldPath,
        lessThan: Any?,
        greaterThan: Any?,
        arrayContains: Any?
    ) = Query(
        (lessThan?.let { js.where(path.js, "<", it) } ?: js).let { js2 ->
            (greaterThan?.let { js2.where(path.js, ">", it) } ?: js2).let { js3 ->
                arrayContains?.let { js3.where(path.js, "array-contains", it) } ?: js3
            }
        }
    )

    internal actual fun _where(field: String, inArray: List<Any>?, arrayContainsAny: List<Any>?) =
        Query(
            (inArray?.let { js.where(field, "in", it.toTypedArray()) } ?: js).let { js2 ->
                arrayContainsAny?.let { js2.where(field, "array-contains-any", it.toTypedArray()) }
                    ?: js2
            }
        )

    internal actual fun _where(path: FieldPath, inArray: List<Any>?, arrayContainsAny: List<Any>?) =
        Query(
            (inArray?.let { js.where(path.js, "in", it.toTypedArray()) } ?: js).let { js2 ->
                arrayContainsAny?.let {
                    js2.where(
                        path.js,
                        "array-contains-any",
                        it.toTypedArray()
                    )
                } ?: js2
            }
        )

    internal actual fun _orderBy(field: String, direction: Direction) = Query(js.orderBy(field, direction.jsString))
    internal actual fun _orderBy(field: FieldPath, direction: Direction) = Query(js.orderBy(field.js, direction.jsString))

    actual val snapshots get() = callbackFlow {
        val unsubscribe = js.onSnapshot(
            { safeOffer(QuerySnapshot(it)) },
            this::close
        )

        awaitClose { unsubscribe() }
    }
}
