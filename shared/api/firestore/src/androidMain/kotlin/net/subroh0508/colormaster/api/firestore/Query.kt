package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.channels.awaitClose
import com.google.firebase.firestore.Query as FirestoreQuery
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Suppress("FunctionName")
actual open class Query(open val android: FirestoreQuery) {

    actual suspend fun get() = QuerySnapshot(android.get().await())

    actual fun limit(limit: Number) = Query(android.limit(limit.toLong()))

    actual val snapshots get() = callbackFlow<QuerySnapshot> {
        val listener = android.addSnapshotListener { snapshot, exception ->
            snapshot?.let { safeOffer(QuerySnapshot(snapshot)) }
            exception?.let { close(exception) }
        }
        awaitClose(listener::remove)
    }

    internal actual fun _where(field: String, equalTo: Any?) = Query(android.whereEqualTo(field, equalTo))
    internal actual fun _where(path: FieldPath, equalTo: Any?) = Query(android.whereEqualTo(path.android, equalTo))

    internal actual fun _where(field: String, equalTo: DocumentReference) = Query(android.whereEqualTo(field, equalTo.android))
    internal actual fun _where(path: FieldPath, equalTo: DocumentReference) = Query(android.whereEqualTo(path.android, equalTo.android))

    internal actual fun _where(field: String, lessThan: Any?, greaterThan: Any?, arrayContains: Any?) = Query(
        (lessThan?.let { android.whereLessThan(field, it) } ?: android).let { android2 ->
            (greaterThan?.let { android2.whereGreaterThan(field, it) } ?: android2).let { android3 ->
                arrayContains?.let { android3.whereArrayContains(field, it) } ?: android3
            }
        }
    )

    internal actual fun _where(path: FieldPath, lessThan: Any?, greaterThan: Any?, arrayContains: Any?) = Query(
        (lessThan?.let { android.whereLessThan(path.android, it) } ?: android).let { android2 ->
            (greaterThan?.let { android2.whereGreaterThan(path.android, it) } ?: android2).let { android3 ->
                arrayContains?.let { android3.whereArrayContains(path.android, it) } ?: android3
            }
        }
    )

    internal actual fun _where(field: String, inArray: List<Any>?, arrayContainsAny: List<Any>?) = Query(
        (inArray?.let { android.whereIn(field, it) } ?: android).let { android2 ->
            arrayContainsAny?.let { android2.whereArrayContainsAny(field, it) } ?: android2
        }
    )

    internal actual fun _where(path: FieldPath, inArray: List<Any>?, arrayContainsAny: List<Any>?) = Query(
        (inArray?.let { android.whereIn(path.android, it) } ?: android).let { android2 ->
            arrayContainsAny?.let { android2.whereArrayContainsAny(path.android, it) } ?: android2
        }
    )

    internal actual fun _orderBy(field: String, direction: Direction) = Query(android.orderBy(field, direction))
    internal actual fun _orderBy(field: FieldPath, direction: Direction) = Query(android.orderBy(field.android, direction))
}
