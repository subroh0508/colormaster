import kotlinx.coroutines.MainScope
import react.dom.render
import utilities.*
import kotlinx.browser.document
import kotlinx.browser.window
import net.subroh0508.colormaster.components.core.FirebaseOptions
import net.subroh0508.colormaster.components.core.initializeApp
import react.Suspense

val mainScope = MainScope()

fun main() {
    initializeApp(FirebaseOptions(
        API_KEY,
        AUTH_DOMAIN,
        DATABASE_URL,
        PROJECT_ID,
        STORAGE_BUCKET,
        MESSAGING_SENDER_ID,
        APP_ID,
        MEASUREMENT_ID,
    ))

    window.onload = {
        render(document.getElementById("root")) {
            I18nextProvider {
                attrs.i18n = i18nextInit()

                Suspense {
                    attrs.asDynamic()["fallback"] = "Loading..."

                    KoinAppProvider { routing() }
                }
            }
        }
    }
}
