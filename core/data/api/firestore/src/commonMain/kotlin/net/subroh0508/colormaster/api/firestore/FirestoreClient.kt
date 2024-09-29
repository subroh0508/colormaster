package net.subroh0508.colormaster.api.firestore

expect class FirestoreClient {
    fun collection(collectionPath: String): CollectionReference
    fun collectionGroup(collectionId: String): Query
    fun document(documentPath: String): DocumentReference
    fun batch(): WriteBatch
    fun setLoggingEnabled(loggingEnabled: Boolean)
    suspend fun clearPersistence()
    suspend fun <T> runTransaction(func: suspend Transaction.() -> T): T
    fun useEmulator(host: String, port: Int)
    fun setSettings(persistenceEnabled: Boolean? = null, sslEnabled: Boolean? = null, host: String? = null, cacheSizeBytes: Long? = null)
    suspend fun disableNetwork()
    suspend fun enableNetwork()
}
