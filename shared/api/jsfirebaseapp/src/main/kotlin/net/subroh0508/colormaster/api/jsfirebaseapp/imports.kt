@file:JsModule("firebase/app")

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

        class EmailAuthProvider : AuthProvider {
            companion object {
                fun credential(email: String, password: String): AuthCredential
            }
        }

        class FacebookAuthProvider : AuthProvider {
            companion object {
                fun credential(token: String): AuthCredential
            }
        }

        class GithubAuthProvider : AuthProvider {
            companion object {
                fun credential(token: String): AuthCredential
            }
        }

        class GoogleAuthProvider : AuthProvider {
            companion object {
                fun credential(idToken: String?, accessToken: String?): AuthCredential
            }
        }

        open class OAuthProvider(providerId: String) : AuthProvider {
            val providerId: String
            fun credential(optionsOrIdToken: Any?, accessToken: String?): AuthCredential

            fun addScope(scope: String)
            fun setCustomParameters(customOAuthParameters: Map<String, String>)
        }

        interface OAuthCredentialOptions {
            val accessToken: String?
            val idToken: String?
            val rawNonce: String?
        }

        class PhoneAuthProvider(auth: Auth?) : AuthProvider {
            companion object {
                fun credential(verificationId: String, verificationCode: String): AuthCredential
            }

            fun verifyPhoneNumber(
                phoneInfoOptions: String,
                applicationVerifier: ApplicationVerifier
            ): Promise<String>
        }

        abstract class ApplicationVerifier {
            val type: String
            fun verify(): Promise<String>
        }

        class TwitterAuthProvider : AuthProvider {
            companion object {
                fun credential(token: String, secret: String): AuthCredential
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
}
