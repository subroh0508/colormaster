import components.templates.MainFrame
import components.templates.RootCompose
import utilities.*
import net.subroh0508.colormaster.common.initializeApp
import net.subroh0508.colormaster.common.koinApp
import net.subroh0508.colormaster.common.ui.ThemeType
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable

fun main() {
    initializeApp(
        API_KEY,
        AUTH_DOMAIN,
        DATABASE_URL,
        PROJECT_ID,
        STORAGE_BUCKET,
        MESSAGING_SENDER_ID,
        APP_ID,
        MEASUREMENT_ID,
    )

    renderComposable(rootElementId = "root") {
        RootCompose(koinApp) { preference, theme ->
            Style(MaterialTheme(theme == ThemeType.NIGHT))

            MainFrame(preference)
        }
    }
}
