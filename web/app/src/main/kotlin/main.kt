import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.components.core.AppModule
import org.kodein.di.Kodein
import react.dom.render
import utilities.*
import kotlin.browser.document
import kotlin.browser.window
import react.Suspense

val mainScope = MainScope()
val appKodein = Kodein { import(AppModule) }

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            I18nextProvider {
                attrs.i18n = i18nextInit()

                Suspense {
                    attrs.asDynamic()["fallback"] = "Loading..."

                    routing()
                }
            }
        }
    }
}
