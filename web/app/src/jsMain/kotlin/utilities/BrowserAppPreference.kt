package utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import kotlinx.browser.localStorage
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.model.ui.commons.ThemeType
import org.koin.dsl.module
import org.w3c.dom.Storage
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.reflect.KProperty

internal val LocalBrowserApp = compositionLocalOf { BrowserAppPreference.State() }

@Composable
internal fun CurrentLocalDarkTheme() = LocalBrowserApp.current.dark

@Composable
internal fun CurrentLocalLanguage() = LocalBrowserApp.current.lang

@Composable
internal fun LocalI18n() = LocalBrowserApp.current.i18n

internal class BrowserAppPreference(
    localStorage: Storage,
    private val state: MutableState<State>,
) : AppPreference {
    override val lang get() = state.value.lang
    override val theme get() = themeType ?: ThemeType.DAY

    override fun setThemeType(type: ThemeType) {
        themeType = type
        state.value = state.value.copy(theme = type)
    }

    private var themeType by localStorage

    data class State(
        override val lang: Languages = Languages.JAPANESE,
        override val theme: ThemeType = ThemeType.DAY,
        val i18n: I18nextText? = null,
    ) : AppPreference.State {
        constructor(localStorage: Storage) : this(
            theme = localStorage["themeType"]?.let {
                ThemeType.valueOf(it.uppercase())
            } ?: ThemeType.DAY,
        )

        val dark = theme == ThemeType.NIGHT
    }
}

internal val Languages.basename get() = when (this) {
    Languages.JAPANESE -> ""
    Languages.ENGLISH -> "/en"
}

private operator fun Storage.getValue(
    thisRef: BrowserAppPreference,
    property: KProperty<*>,
) = get(property.name)?.let { ThemeType.valueOf(it.uppercase()) }

private operator fun Storage.setValue(
    thisRef: BrowserAppPreference,
    property: KProperty<*>,
    value: ThemeType?,
) { if (value == null) removeItem(property.name) else set(property.name, value.name) }

@Suppress("FunctionName")
internal fun AppPreferenceModule(
    state: MutableState<BrowserAppPreference.State>,
) = module {
    single<AppPreference> { BrowserAppPreference(localStorage, state) }
}
