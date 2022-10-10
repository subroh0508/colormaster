import androidx.compose.runtime.compositionLocalOf
import components.templates.MainFrame
import components.templates.RootCompose
import utilities.*
import net.subroh0508.colormaster.components.core.FirebaseOptions
import net.subroh0508.colormaster.components.core.initializeApp
import net.subroh0508.colormaster.presentation.common.ui.ThemeType
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.koin.dsl.koinApplication

private val koinApp = koinApplication { }

val LocalKoinApp = compositionLocalOf { koinApp }

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

    renderComposable(rootElementId = "root") {
        RootCompose(koinApp) { preference, theme ->
            Style(MaterialTheme(theme == ThemeType.NIGHT))

            MainFrame(preference)
        }
    }
}
