@file:JsModule("firebase/compat/app")
@file:JsNonModule

package net.subroh0508.colormaster.api.jsfirebaseapp

import kotlin.js.Json
import kotlin.js.Promise

@Suppress("ClassName")
@JsName("default")
external object firebase {
    fun initializeApp(options: Any, name: String? = definedExternally) : App

    open class App {
        val name: String
        val options: Options
    }

    interface Options {
        var applicationId: String
        var apiKey: String
        var databaseURL: String?
        var gaTrackingId: String?
        var storageBucket: String?
        var projectId: String?
        var messagingSenderId: String?
        var authDomain: String?
        var appId: String?
        var measurementId: String?
    }

    interface FirebaseError {
        var code: String
        var message: String
        var name: String
    }

    fun auth(app: App? = definedExternally): auth.Auth

    object auth {
        open class Auth {
            val currentUser: user.User?
            var languageCode: String?

            fun useEmulator(url: String)
            fun applyActionCode(code: String): Promise<Unit>
            fun checkActionCode(code: String): Promise<ActionCodeInfo>
            fun confirmPasswordReset(code: String, newPassword: String): Promise<Unit>
            fun createUserWithEmailAndPassword(email: String, password: String): Promise<AuthResult>
            fun fetchSignInMethodsForEmail(email: String): Promise<Array<String>>
            fun sendPasswordResetEmail(email: String, actionCodeSettings: Any?): Promise<Unit>
            fun sendSignInLinkToEmail(email: String, actionCodeSettings: Any?): Promise<Unit>
            fun signInWithEmailAndPassword(email: String, password: String): Promise<AuthResult>
            fun signInWithCustomToken(token: String): Promise<AuthResult>
            fun signInAnonymously(): Promise<AuthResult>
            fun signInWithCredential(authCredential: AuthCredential): Promise<AuthResult>
            fun signInWithPopup(provider: AuthProvider): Promise<AuthResult>
            fun signInWithRedirect(provider: AuthProvider): Promise<Unit>
            fun getRedirectResult(): Promise<AuthResult>
            fun signOut(): Promise<Unit>
            fun updateCurrentUser(user: user.User?): Promise<Unit>
            fun verifyPasswordResetCode(code: String): Promise<String>

            fun onAuthStateChanged(nextOrObserver: (user.User?) -> Unit): () -> Unit
            fun onIdTokenChanged(nextOrObserver: (user.User?) -> Unit): () -> Unit
        }

        abstract class IdTokenResult {
            val authTime: String
            val claims: Json
            val expirationTime: String
            val issuedAtTime: String
            val signInProvider: String?
            val signInSecondFactor: String?
            val token: String
        }

        abstract class AuthResult {
            val credential: AuthCredential?
            val operationType: String?
            val user: user.User?
        }

        abstract class AuthCredential {
            val providerId: String
            val signInMethod: String
        }

        abstract class ActionCodeInfo {
            val operation: String
            val data: ActionCodeData
        }

        abstract class ActionCodeData {
            val email: String?
            val previousEmail: String?
        }

        interface ActionCodeSettings {
            val android: AndroidActionCodeSettings?
            val dynamicLinkDomain: String?
            val handleCodeInApp: Boolean?
            val iOS: iOSActionCodeSettings?
            val url: String
        }

        interface AndroidActionCodeSettings {
            val installApp: Boolean?
            val minimumVersion: String?
            val packageName: String
        }

        interface iOSActionCodeSettings {
            val bundleId: String?
        }

        interface AuthProvider

        class GoogleAuthProvider : AuthProvider {
            companion object {
                fun credential(idToken: String?, accessToken: String?): AuthCredential
            }
        }
    }

    object user {
        abstract class User {
            val uid: String
            val displayName: String?
            val email: String?
            val emailVerified: Boolean
            val metadata: UserMetadata
            val phoneNumber: String?
            val photoURL: String?
            val providerData: Array<UserInfo>
            val providerId: String
            val refreshToken: String
            val tenantId: String?
            val isAnonymous: Boolean

            fun delete(): Promise<Unit>
            fun getIdToken(forceRefresh: Boolean?): Promise<String>
            fun getIdTokenResult(forceRefresh: Boolean?): Promise<auth.IdTokenResult>
            fun linkWithCredential(credential: auth.AuthCredential): Promise<auth.AuthResult>
            fun reauthenticateWithCredential(credential: auth.AuthCredential): Promise<auth.AuthResult>
            fun reload(): Promise<Unit>
            fun sendEmailVerification(actionCodeSettings: Any?): Promise<Unit>
            fun unlink(providerId: String): Promise<User>
            fun updateEmail(newEmail: String): Promise<Unit>
            fun updatePassword(newPassword: String): Promise<Unit>
            fun updatePhoneNumber(phoneCredential: auth.AuthCredential): Promise<Unit>
            fun updateProfile(profile: ProfileUpdateRequest): Promise<Unit>
            fun verifyBeforeUpdateEmail(newEmail: String, actionCodeSettings: Any?): Promise<Unit>
        }

        abstract class UserMetadata {
            val creationTime: String?
            val lastSignInTime: String?
        }

        abstract class UserInfo {
            val displayName: String?
            val email: String?
            val phoneNumber: String?
            val photoURL: String?
            val providerId: String
            val uid: String
        }

        interface ProfileUpdateRequest {
            val displayName: String?
            val photoURL: String?
        }
    }

    fun firestore(): firestore.Firestore

    object firestore {
        fun setLogLevel(level: String)

        open class PersistenceSettings {
            var experimentalTabSynchronization: Boolean
        }

        open class Firestore {
            fun <T> runTransaction(func: (transaction: Transaction) -> Promise<T>): Promise<T>
            fun batch(): WriteBatch
            fun collection(collectionPath: String): CollectionReference
            fun collectionGroup(collectionId: String): Query
            fun doc(documentPath: String): DocumentReference
            fun settings(settings: Json)
            fun enablePersistence(): Promise<Unit>
            fun clearPersistence(): Promise<Unit>
            fun useEmulator(host: String, port: Int)
            fun disableNetwork(): Promise<Unit>
            fun enableNetwork(): Promise<Unit>
        }

        open class Timestamp {
            val seconds: Double
            val nanoseconds: Double
            fun toMillis(): Double
        }

        open class Query {
            fun get(options: Any? = definedExternally): Promise<QuerySnapshot>
            fun where(field: String, opStr: String, value: Any?): Query
            fun where(field: FieldPath, opStr: String, value: Any?): Query
            fun onSnapshot(next: (snapshot: QuerySnapshot) -> Unit, error: (error: Error) -> Unit): () -> Unit
            fun limit(limit: Double): Query
            fun orderBy(field: String, direction: Any): Query
            fun orderBy(field: FieldPath, direction: Any): Query
        }

        open class CollectionReference : Query {
            val path: String
            fun doc(path: String = definedExternally): DocumentReference
            fun add(data: Any): Promise<DocumentReference>
        }

        open class QuerySnapshot {
            val docs: Array<DocumentSnapshot>
            fun docChanges(): Array<DocumentChange>
            val empty: Boolean
            val metadata: SnapshotMetadata
        }

        open class DocumentChange {
            val doc: DocumentSnapshot
            val newIndex: Int
            val oldIndex: Int
            val type: String
        }

        open class DocumentSnapshot {
            val id: String
            val ref: DocumentReference
            val exists: Boolean
            val metadata: SnapshotMetadata
            fun data(options: Any? = definedExternally): Any?
            fun get(fieldPath: String, options: Any? = definedExternally): Any?
            fun get(fieldPath: FieldPath, options: Any? = definedExternally): Any?
        }

        open class SnapshotMetadata {
            val hasPendingWrites: Boolean
            val fromCache: Boolean
        }

        open class DocumentReference {
            val id: String
            val path: String

            fun collection(path: String): CollectionReference
            fun get(options: Any? = definedExternally): Promise<DocumentSnapshot>
            fun set(data: Any, options: Any? = definedExternally): Promise<Unit>
            fun update(data: Any): Promise<Unit>
            fun update(field: String, value: Any?, vararg moreFieldsAndValues: Any?): Promise<Unit>
            fun update(field: FieldPath, value: Any?, vararg moreFieldsAndValues: Any?): Promise<Unit>
            fun delete(): Promise<Unit>
            fun onSnapshot(next: (snapshot: DocumentSnapshot) -> Unit, error: (error: Error) -> Unit): ()->Unit
        }

        open class WriteBatch {
            fun commit(): Promise<Unit>
            fun delete(documentReference: DocumentReference): WriteBatch
            fun set(documentReference: DocumentReference, data: Any, options: Any? = definedExternally): WriteBatch
            fun update(documentReference: DocumentReference, data: Any): WriteBatch
            fun update(documentReference: DocumentReference, field: String, value: Any?, vararg moreFieldsAndValues: Any?): WriteBatch
            fun update(documentReference: DocumentReference, field: FieldPath, value: Any?, vararg moreFieldsAndValues: Any?): WriteBatch
        }

        open class Transaction {
            fun get(documentReference: DocumentReference): Promise<DocumentSnapshot>
            fun set(documentReference: DocumentReference, data: Any, options: Any? = definedExternally): Transaction
            fun update(documentReference: DocumentReference, data: Any): Transaction
            fun update(documentReference: DocumentReference, field: String, value: Any?, vararg moreFieldsAndValues: Any?): Transaction
            fun update(documentReference: DocumentReference, field: FieldPath, value: Any?, vararg moreFieldsAndValues: Any?): Transaction
            fun delete(documentReference: DocumentReference): Transaction
        }

        open class FieldPath(vararg fieldNames: String) {
            companion object {
                val documentId: FieldPath
            }
        }

        abstract class FieldValue {
            companion object {
                fun serverTimestamp(): FieldValue
                fun delete(): FieldValue
                fun arrayRemove(vararg elements: Any): FieldValue
                fun arrayUnion(vararg elements: Any): FieldValue
            }
        }
    }
}
