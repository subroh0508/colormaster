import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import components.templates.MainFrame
import components.templates.RootCompose
import utilities.*
import net.subroh0508.colormaster.components.core.FirebaseOptions
import net.subroh0508.colormaster.components.core.initializeApp
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.model.ui.commons.I18n
import net.subroh0508.colormaster.model.ui.commons.ThemeType
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

    renderComposable(rootElementId = "root") {
        RootCompose(i18nextInit()) { preference ->
            Style(MaterialTheme(preference.themeType == ThemeType.NIGHT))

            MainFrame(preference)
        }
    }
}
