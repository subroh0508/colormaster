package net.subroh0508.colormaster.api.firestore

import kotlinx.coroutines.flow.Flow

@Suppress("FunctionName")
expect open class Query {
    fun limit(limit: Number): Query
    val snapshots: Flow<QuerySnapshot>
    suspend fun get(): QuerySnapshot
    internal fun _where(field: String, equalTo: Any?): Query
    internal fun _where(path: FieldPath, equalTo: Any?): Query
    internal fun _where(field: String, equalTo: DocumentReference): Query
    internal fun _where(path: FieldPath, equalTo: DocumentReference): Query
    internal fun _where(field: String, lessThan: Any? = null, greaterThan: Any? = null, arrayContains: Any? = null): Query
    internal fun _where(path: FieldPath, lessThan: Any? = null, greaterThan: Any? = null, arrayContains: Any? = null): Query
    internal fun _where(field: String, inArray: List<Any>? = null, arrayContainsAny: List<Any>? = null): Query
    internal fun _where(path: FieldPath, inArray: List<Any>? = null, arrayContainsAny: List<Any>? = null): Query

    internal fun _orderBy(field: String, direction: Direction): Query
    internal fun _orderBy(field: FieldPath, direction: Direction): Query
}

fun Query.where(field: String, equalTo: Any?) = _where(field, equalTo)
fun Query.where(path: FieldPath, equalTo: Any?) = _where(path, equalTo)
fun Query.where(field: String, equalTo: DocumentReference) = _where(field, equalTo)
fun Query.where(path: FieldPath, equalTo: DocumentReference) = _where(path, equalTo)
fun Query.where(field: String, lessThan: Any? = null, greaterThan: Any? = null, arrayContains: Any? = null) = _where(field, lessThan, greaterThan, arrayContains)
fun Query.where(path: FieldPath, lessThan: Any? = null, greaterThan: Any? = null, arrayContains: Any? = null) = _where(path, lessThan, greaterThan, arrayContains)
fun Query.where(field: String, inArray: List<Any>? = null, arrayContainsAny: List<Any>? = null) = _where(field, inArray, arrayContainsAny)
fun Query.where(path: FieldPath, inArray: List<Any>? = null, arrayContainsAny: List<Any>? = null) = _where(path, inArray, arrayContainsAny)

fun Query.orderBy(field: String, direction: Direction = Direction.ASCENDING) = _orderBy(field, direction)
fun Query.orderBy(field: FieldPath, direction: Direction = Direction.ASCENDING) = _orderBy(field, direction)
