import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.components.core.AppModule
import org.kodein.di.Kodein
import react.dom.render
import utilities.*
import kotlin.browser.document
import kotlin.browser.window
import kotlinext.js.require

val mainScope = MainScope()
val appKodein = Kodein { import(AppModule) }
lateinit var i18n: I18next

fun main() {
    window.onload = {
        i18n = i18next.
            use(httpBackend).
            use(ReactI18next.initReactI18next).
            apply {
                init {
                    resources("ja", require("locale/ja"))
                    lng = "ja"
                    fallbackLng("ja")
                    partialBundledLanguages = true
                    backend {
                        loadPath = "/locale/{{lng}}.json"
                    }
                }
            }

        render(document.getElementById("root")) {
            I18nextProvider {
                attrs.i18n = i18n

                routing()
            }
        }
    }
}
