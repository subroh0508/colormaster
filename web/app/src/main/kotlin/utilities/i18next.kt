@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package utilities

import kotlinext.js.jsObject
import kotlin.js.Promise
import kotlinext.js.require

@JsModule("i18next")
private external val i18nextModule: dynamic
@JsModule("i18next-http-backend")
private external val httpBackendModule: dynamic

private val i18next: I18next = i18nextModule.default as I18next
private val httpBackend: dynamic = httpBackendModule.default

external interface I18next {
    fun init(options: I18nextOptions, callback: dynamic): Promise<I18nextText>
    fun use(module: dynamic): I18next
    fun changeLanguage(lng: String): Promise<I18nextText>
    fun t(vararg arg: Any): String
    val languages: Array<String>
    val language: String
}

external interface I18nextText

external interface I18nextOptions {
    var lng: String
    var debug: Boolean?
    var resources: I18nextResources
    var partialBundledLanguages: Boolean?
}

external interface I18nextResources

external interface I18nextBackendOptions {
    var loadPath: String
}

fun I18nextOptions.resources(code: String, res: dynamic) { resources = jsObject { this[code] = res } }
fun I18nextOptions.fallbackLng(vararg lng: String) { this.asDynamic()["fallbackLng"] = lng }
fun I18nextOptions.backend(options: I18nextBackendOptions.() -> Unit) { this.asDynamic()["backend"] = jsObject(options) }
fun I18nextResources.ja(res: dynamic) { this.asDynamic()["ja.translation"] = res }
operator fun I18nextResources.set(code: String, res: dynamic) { this.asDynamic()[code] = js("{ translation: res }") }

fun I18next.init(options: I18nextOptions.() -> Unit) = i18next.init(jsObject(options), undefined)
operator fun I18nextText.invoke(key: String): String = asDynamic()(key) as String
operator fun I18nextText.invoke(key: String, args: Any): String = asDynamic()(key, args) as String

fun i18nextInit() = i18next.
    use(httpBackend).
    use(ReactI18next.initReactI18next).
    apply {
        init {
            resources("ja", require("locale/ja"))
            lng = "ja"
            debug = true
            fallbackLng("ja")
            partialBundledLanguages = true
            backend {
                loadPath = "/locale/{{lng}}.json"
            }
        }
    }
