package components.templates

import androidx.compose.runtime.*
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.koin.dsl.koinApplication
import utilities.*
import utilities.AppPreferenceModule
import utilities.BrowserAppPreference
import utilities.LocalBrowserApp

@Composable
fun RootCompose(
    i18next: I18next,
    content: @Composable (AppPreference) -> Unit,
) {
    var appState by remember { mutableStateOf(BrowserAppPreference.State()) }

    val koinApplication = koinApplication {
        modules(AppPreferenceModule(i18next) { appState = it })
    }

    CompositionLocalProvider(
        LocalBrowserApp provides appState
    ) { content(koinApplication.koin.get()) }
}


