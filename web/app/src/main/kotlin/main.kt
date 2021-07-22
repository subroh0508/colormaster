import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.components.core.AppModule
import org.koin.dsl.koinApplication
import react.dom.render
import utilities.*
import kotlinx.browser.document
import kotlinx.browser.window
import net.subroh0508.colormaster.components.core.initializeApp
import org.koin.core.KoinApplication
import react.Suspense
import react.createContext

val mainScope = MainScope()
val koinApp = koinApplication {
    modules(AppModule + AppPreferenceModule)
}

val KoinAppContext = createContext<KoinApplication>()

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            I18nextProvider {
                attrs.i18n = i18nextInit()

                Suspense {
                    attrs.asDynamic()["fallback"] = "Loading..."

                    KoinAppContext.Provider {
                        attrs.value = koinApp

                        routing()
                    }
                }
            }
        }
    }
}
