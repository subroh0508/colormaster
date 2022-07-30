package components.templates

import androidx.compose.runtime.*
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.koin.dsl.koinApplication
import utilities.*
import utilities.AppPreferenceModule
import utilities.BrowserAppPreference
import utilities.LocalBrowserApp

@Composable
fun RootCompose(
    lang: Languages,
    i18n: I18nextText,
    content: @Composable (AppPreference) -> Unit,
) {
    var appState by remember { mutableStateOf(BrowserAppPreference.State(i18n = i18n)) }

    val koinApplication = koinApplication {
        modules(AppPreferenceModule(lang, i18n) { appState = it })
    }

    CompositionLocalProvider(
        LocalBrowserApp provides appState,
    ) { content(koinApplication.koin.get()) }
}


