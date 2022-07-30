package utilities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import kotlinx.browser.localStorage
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.model.ui.commons.I18n
import net.subroh0508.colormaster.model.ui.commons.ThemeType
import org.koin.dsl.module
import org.w3c.dom.Storage
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.reflect.KProperty

internal val LocalBrowserApp: ProvidableCompositionLocal<BrowserAppPreference.State> = compositionLocalOf {
    BrowserAppPreference.State()
}

@Composable
internal fun LocalI18n() = LocalBrowserApp.current.i18n

internal class BrowserAppPreference(
    localStorage: Storage,
    private val i18next: I18next,
    private val onChange: (State) -> Unit,
) : AppPreference, I18n {
    override val lang get() = language ?: Languages.JAPANESE
    override val themeType get() = paletteType

    override fun setLanguage(lang: Languages) {
        language = lang
        onChange(State(this))
    }
    override fun setThemeType(type: ThemeType) {
        paletteType = type
        onChange(State(this))
    }
    override fun toggleThemeType() {
        paletteType = if (paletteType == ThemeType.DAY) ThemeType.NIGHT else ThemeType.DAY
        onChange(State(this))
    }

    override fun t(vararg arg: Any) = i18next.t(*arg)

    private var language by i18next
    private var paletteType by localStorage

    data class State(
        val lang: Languages = Languages.JAPANESE,
        val themeType: ThemeType = ThemeType.DAY,
        val i18n: I18n? = null,
    ) {
        constructor(preference: BrowserAppPreference) : this(
            lang = preference.lang,
            themeType = preference.themeType ?: ThemeType.DAY,
            i18n = preference,
        )
    }
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
