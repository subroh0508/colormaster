package utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import kotlinx.browser.localStorage
import kotlinx.browser.window
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.model.ui.commons.I18n
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
internal fun LocalI18n() = LocalBrowserApp.current.i18n

internal class BrowserAppPreference(
    localStorage: Storage,
    override val lang: Languages,
    private val i18n: I18nextText,
    private val onChange: (State) -> Unit,
) : AppPreference {
    override val theme get() = themeType ?: ThemeType.DAY

    override fun setLanguage(lang: Languages) = Unit
    override fun setThemeType(type: ThemeType) {
        themeType = type
        onChange(State(this, i18n))
    }

    private var themeType by localStorage

    data class State(
        override val lang: Languages = Languages.JAPANESE,
        override val theme: ThemeType = ThemeType.DAY,
        val i18n: I18nextText? = null,
    ) : AppPreference.State {
        constructor(preference: BrowserAppPreference, i18n: I18nextText) : this(
            lang = preference.lang,
            theme = preference.themeType ?: ThemeType.DAY,
            i18n = i18n,
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
    lang: Languages,
    i18n: I18nextText,
    onChange: (BrowserAppPreference.State) -> Unit,
) = module {
    val preference = BrowserAppPreference(localStorage, lang, i18n, onChange)

    single<AppPreference> { preference }

    onChange(BrowserAppPreference.State(preference, i18n))
}
