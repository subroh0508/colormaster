import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.components.core.AppModule
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import react.dom.render
import utilities.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.koin.core.logger.PrintLogger
import react.Suspense

val mainScope = MainScope()
val koinApp = koinApplication {
    modules(AppModule + AppPreferenceModule)
}

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
