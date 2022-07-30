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

val LocalBrowserApp: ProvidableCompositionLocal<AppPreference.State> = compositionLocalOf {
    BrowserAppPreference.State()
}

@Composable
internal fun LocalI18n() = LocalBrowserApp.current.i18n

internal class BrowserAppPreference(
    localStorage: Storage,
    private val i18next: I18next,
    private val onChange: (State) -> Unit,
) : AppPreference {
    override val lang get() = language ?: Languages.JAPANESE
    override val theme get() = themeType ?: ThemeType.DAY

    override fun setLanguage(lang: Languages) {
        val next = window.location.pathname.replace(language?.basename ?: "", "")

        window.location.replace(lang.basename + next)
    }
    override fun setThemeType(type: ThemeType) {
        themeType = type
        onChange(State(this))
    }

    private var language by i18next
    private var themeType by localStorage

    private val Languages.basename get() = when (this) {
        Languages.JAPANESE -> ""
        Languages.ENGLISH -> "/en"
    }

    data class State(
        override val lang: Languages = Languages.JAPANESE,
        override val theme: ThemeType = ThemeType.DAY,
        val i18n: I18next? = null,
    ) : AppPreference.State {
        constructor(preference: BrowserAppPreference) : this(
            lang = preference.language ?: Languages.JAPANESE,
            theme = preference.themeType ?: ThemeType.DAY,
            i18n = preference.i18next,
        )
    }
}

internal val AppPreference.State.i18n get() = (this as? BrowserAppPreference.State)?.i18n
internal operator fun AppPreference.State.component3() = i18n

private operator fun Storage.getValue(
    thisRef: BrowserAppPreference,
    property: KProperty<*>,
) = get(property.name)?.let { ThemeType.valueOf(it.uppercase()) }

private operator fun Storage.setValue(
    thisRef: BrowserAppPreference,
    property: KProperty<*>,
    value: ThemeType?,
) { if (value == null) removeItem(property.name) else set(property.name, value.name) }

private operator fun I18next.getValue(
    thisRef: BrowserAppPreference,
    property: KProperty<*>,
) = Languages.valueOfCode(language)

private operator fun I18next.setValue(
    thisRef: BrowserAppPreference,
    property: KProperty<*>,
    value: Languages?,
) { value?.code?.let(this::changeLanguage) }

@Suppress("FunctionName")
internal fun AppPreferenceModule(
    i18next: I18next,
    onChange: (BrowserAppPreference.State) -> Unit,
) = module {
    val preference = BrowserAppPreference(localStorage, i18next, onChange)

    single<AppPreference> { preference }

    onChange(BrowserAppPreference.State(preference))
}
