package components.templates

import androidx.compose.runtime.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.browser.localStorage
import kotlinx.browser.window
import net.subroh0508.colormaster.repository.di.DataModule
import net.subroh0508.colormaster.components.core.LocalKoinApp
import net.subroh0508.colormaster.components.core.external.I18next
import net.subroh0508.colormaster.components.core.external.i18nextInit
import net.subroh0508.colormaster.components.core.external.onLanguageChanged
import net.subroh0508.colormaster.components.core.ui.AppPreference
import net.subroh0508.colormaster.components.core.ui.LocalApp
import net.subroh0508.colormaster.components.core.ui.ThemeType
import net.subroh0508.colormaster.components.core.ui.basename
import org.koin.core.KoinApplication
import routes.LocalRouter
import routes.Router

@Composable
fun RootCompose(
    koinApp: KoinApplication,
    content: @Composable (AppPreference, ThemeType) -> Unit,
) = I18nextCompose(koinApp) { preference, appState, i18next ->
    val lifecycle = remember(koinApp) { LifecycleRegistry() }
    val router = remember(koinApp) { Router(DefaultComponentContext(lifecycle), window, i18next) }

    CompositionLocalProvider(
        LocalKoinApp provides koinApp,
        LocalApp provides appState,
        LocalRouter provides router,
    ) { preference?.let { content(it, appState.theme) } }
}

@Composable
private fun I18nextCompose(
    koinApp: KoinApplication,
    content: @Composable (AppPreference?, AppPreference.State, I18next) -> Unit,
) {
    val appState = remember { mutableStateOf(AppPreference.State(localStorage)) }
    val i18next = remember(koinApp) { mutableStateOf<I18next?>(null) }

    val preference = remember(koinApp) { AppPreference(localStorage, appState) }

    SideEffect {
        koinApp.modules(DataModule)

        if (appState.value.i18n != null) {
            return@SideEffect
        }

        i18next.value = i18nextInit(window) { _, i18n, language ->
            appState.value = appState.value.copy(lang = language, i18n = i18n)
        }.onLanguageChanged {
            if (it == null || it == appState.value.lang) {
                return@onLanguageChanged
            }

            val path = window.location.pathname.replace(
                appState.value.lang.basename,
                "",
            ) + window.location.search

            window.location.replace(it.basename + path)
        }
    }

    i18next.value?.let { content(preference, appState.value, it) }
}
