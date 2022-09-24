@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package utilities

import kotlinx.js.jso
import kotlin.js.Promise
import kotlinext.js.require
import net.subroh0508.colormaster.model.Languages
import org.w3c.dom.Window

@JsModule("i18next/i18next")
private external val i18next: I18next

external interface I18next {
    fun init(options: I18nextOptions, callback: dynamic): Promise<I18nextText>
    fun use(module: dynamic): I18next
    fun changeLanguage(lng: String): Promise<I18nextText>
    fun t(vararg arg: Any): String

    fun on(event: String, callback: dynamic)

    val languages: Array<String>
    val language: String
}

@JsModule("i18next-http-backend")
private external val httpBackendModule: dynamic

private val httpBackend = if (httpBackendModule.default != undefined) httpBackendModule.default else httpBackendModule

external interface I18nextText

external interface I18nextOptions {
    var lng: String
    var debug: Boolean?
    var resources: I18nextResources
    var partialBundledLanguages: Boolean?
    var nsSeparator: Boolean?
}

external interface I18nextResources

external interface I18nextBackendOptions {
    var loadPath: String
}

fun I18nextOptions.resources(code: String, res: dynamic) { resources = jso { this[code] = res } }
fun I18nextOptions.fallbackLng(vararg lng: String) { this.asDynamic()["fallbackLng"] = lng }
fun I18nextOptions.backend(options: I18nextBackendOptions.() -> Unit) { this.asDynamic()["backend"] = jso(options) }
fun I18nextResources.ja(res: dynamic) { this.asDynamic()["ja.translation"] = res }
operator fun I18nextResources.set(code: String, res: dynamic) { this.asDynamic()[code] = js("{ translation: res }") }

fun I18next.init(options: I18nextOptions.() -> Unit, callback: (Error, I18nextText) -> Unit) = init(jso(options), callback)
operator fun I18nextText.invoke(key: String): String = asDynamic()(key) as String
operator fun I18nextText.invoke(key: String, args: Any): String = asDynamic()(key, args) as String

fun i18nextInit(
    window: Window,
    func: (Error, I18nextText, Languages) -> Unit,
): I18next {
    val language = window.language

    return i18next
        .use(httpBackend)
        .apply {
            init(options = {
                    resources(language.code, require("locale/${language.code}"))
                    lng = language.code
                    fallbackLng("ja")
                    nsSeparator = false
                    partialBundledLanguages = true
                    backend {
                        loadPath = "/locale/{{lng}}.json"
                    }
                },
                callback = { e, t -> func(e, t, language) },
            )
        }
}

fun I18next.onLanguageChanged(func: (Languages?) -> Unit) = apply {
    on("languageChanged") { code: String -> func(Languages.valueOfCode(code)) }
}

internal val Window.language
    get() = window.location.pathname.split("/")[1].takeIf(String::isNotBlank)?.let { code ->
        Languages.valueOfCode(code)
    } ?: Languages.JAPANESE
