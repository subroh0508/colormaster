@file:JsModule("firebase/app")

package net.subroh0508.colormaster.api.jsfirebaseapp

@JsName("default")
external object firebase {
    open class App {
        val name: String
        val options: Options
    }

    interface Options {
        val applicationId: String
        val apiKey: String
        val databaseUrl: String?
        val gaTrackingId: String?
        val storageBucket: String?
        val projectId: String?
    }

    interface FirebaseError {
        var code: String
        var message: String
        var name: String
    }

    val apps : Array<App>
    fun app(name: String? = definedExternally): App
    fun initializeApp(options: Any, name: String? = definedExternally) : App
}
