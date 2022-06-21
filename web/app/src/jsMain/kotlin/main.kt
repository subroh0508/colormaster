import androidx.compose.runtime.Composable
import components.templates.MainFrame
import material.components.TopAppBar
import utilities.*
import net.subroh0508.colormaster.components.core.FirebaseOptions
import net.subroh0508.colormaster.components.core.initializeApp
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

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

    renderComposable(rootElementId = "root") { App() }
}

@Composable
private fun App() {
    Style(MaterialTheme())

    MainFrame()
}
