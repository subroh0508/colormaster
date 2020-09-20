import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.components.core.AppModule
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import react.dom.render
import utilities.*
import kotlin.browser.document
import kotlin.browser.window
import react.Suspense

val mainScope = MainScope()
val appDI = koinApplication {
    modules(AppModule + AppPreferenceModule)
}

fun main() {
    window.onload = {
        startKoin {
            modules()
        }

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
