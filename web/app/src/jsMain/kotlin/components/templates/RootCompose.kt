package components.templates

import LocalKoinApp
import androidx.compose.runtime.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.browser.window
import net.subroh0508.colormaster.components.core.AppModule
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.koin.core.KoinApplication
import routes.LocalRouter
import routes.Router
import utilities.*
import utilities.AppPreferenceModule
import utilities.BrowserAppPreference
import utilities.LocalBrowserApp

@Composable
fun RootCompose(
    koinApp: KoinApplication,
    content: @Composable (AppPreference) -> Unit,
) = I18nextCompose(koinApp) { preference, appState, i18next ->
    val lifecycle = remember(koinApp) { LifecycleRegistry() }
    val router = remember(koinApp) { Router(DefaultComponentContext(lifecycle), window, i18next) }

    CompositionLocalProvider(
        LocalKoinApp provides koinApp,
        LocalBrowserApp provides appState,
        LocalRouter provides router,
    ) { preference?.let { content(it) } }
}

@Composable
private fun I18nextCompose(
    koinApp: KoinApplication,
    content: @Composable (AppPreference?, BrowserAppPreference.State, I18next) -> Unit,
) {
    val appState = remember { mutableStateOf(BrowserAppPreference.State()) }
    val preference = remember(koinApp) { mutableStateOf<AppPreference?>(null) }
    val i18next = remember(koinApp) { mutableStateOf<I18next?>(null) }

    SideEffect {
        if (appState.value.i18n != null) {
            return@SideEffect
        }

        i18next.value = i18nextInit(window) { _, i18n, language ->
            appState.value = appState.value.copy(lang = language, i18n = i18n)

            koinApp.modules(AppModule + AppPreferenceModule(language, i18n) { appState.value = it })
            preference.value = koinApp.koin.get()
        }.onLanguageChanged {
            if (it == null || it == appState.value.lang) {
                return@onLanguageChanged
            }

            val path = window.location.pathname.replace(appState.value.lang.basename, "")

            window.location.replace(it.basename + path)
        }
    }

    i18next.value?.let { content(preference.value, appState.value, it) }
}
