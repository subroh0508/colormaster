import kotlinx.coroutines.MainScope
import net.subroh0508.colormaster.components.core.AppModule
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import react.dom.render
import utilities.*
import kotlinx.browser.document
import kotlinx.browser.window
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import org.koin.dsl.module
import react.Suspense

val mainScope = MainScope()
val appDI = koinApplication {
    modules(AppModule + AppPreferenceModule + module {
        single { PreviewViewModel(get()) }
    })
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
