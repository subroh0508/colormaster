import components.templates.MainFrame
import components.templates.RootCompose
import kotlinx.browser.window
import utilities.*
import net.subroh0508.colormaster.components.core.FirebaseOptions
import net.subroh0508.colormaster.components.core.initializeApp
import net.subroh0508.colormaster.model.ui.commons.ThemeType
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.url.URLSearchParams

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

    val i18next = i18nextInit()

    i18next.changeLanguage(window)
        .then {
            renderComposable(rootElementId = "root") {
                RootCompose(i18next) { preference ->
                    Style(MaterialTheme(preference.theme == ThemeType.NIGHT))

                    MainFrame(preference)
                }
            }
        }
}
