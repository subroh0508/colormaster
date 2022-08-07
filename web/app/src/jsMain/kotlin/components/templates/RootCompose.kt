package components.templates

import CurrentLocalKoinApp
import LocalKoinApp
import androidx.compose.runtime.*
import net.subroh0508.colormaster.components.core.AppModule
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.koin.core.KoinApplication
import utilities.*
import utilities.AppPreferenceModule
import utilities.BrowserAppPreference
import utilities.LocalBrowserApp

@Composable
fun RootCompose(
    koinApp: KoinApplication,
    lang: Languages,
    i18n: I18nextText,
    content: @Composable (AppPreference) -> Unit,
) {
    val appState = remember { mutableStateOf(BrowserAppPreference.State(i18n = i18n)) }

    CompositionLocalProvider(
        LocalKoinApp provides koinApp,
    ) { Content(lang, i18n, appState, content) }
}

@Composable
private fun Content(
    lang: Languages,
    i18n: I18nextText,
    appState: MutableState<BrowserAppPreference.State>,
    content: @Composable (AppPreference) -> Unit,
) {
    val koinApp = CurrentLocalKoinApp()
    val preference = remember { mutableStateOf<AppPreference?>(null) }

    SideEffect {
        koinApp.modules(AppModule + AppPreferenceModule(lang, i18n) { appState.value = it })

        preference.value = koinApp.koin.get()
    }

    CompositionLocalProvider(
        LocalBrowserApp provides appState.value,
    ) { preference.value?.let { content(it) } }
}


