import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import components.templates.MainFrame
import utilities.*
import net.subroh0508.colormaster.components.core.FirebaseOptions
import net.subroh0508.colormaster.components.core.initializeApp
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.model.ui.commons.I18n
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.koin.dsl.koinApplication

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

    val koinApp = koinApplication {
        modules(AppPreferenceModule(i18nextInit()))
    }

    renderComposable(rootElementId = "root") {
        App(koinApp.koin.get(), koinApp.koin.get())
    }
}

@Composable
private fun App(
    i18n: I18n,
    preference: AppPreference,
) = CompositionLocalProvider(
    LocalBrowserApp provides (i18n to preference),
) {
    Style(MaterialTheme())

    MainFrame()
}
