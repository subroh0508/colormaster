package utilities

import kotlinx.browser.localStorage
import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.model.ui.commons.ThemeType
import org.koin.dsl.module
import org.w3c.dom.Storage
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.reflect.KProperty

internal class BrowserAppPreference(
    localStorage: Storage,
    i18next: I18next,
) : AppPreference {
    override val lang get() = language ?: Languages.JAPANESE
    override val themeType get() = paletteType

    override fun setLanguage(lang: Languages) { language = lang }
    override fun setThemeType(type: ThemeType) { paletteType = type }

    private var language by i18next
    private var paletteType by localStorage

    data class State(
        val lang: Languages,
        val themeType: ThemeType,
    ) {
        constructor(preference: AppPreference) : this(
            lang = preference.lang ?: Languages.JAPANESE,
            themeType = preference.themeType ?: ThemeType.light,
        )
    }
}

private operator fun Storage.getValue(
    thisRef: BrowserAppPreference,
    property: KProperty<*>,
) = get(property.name)?.let { ThemeType.valueOf(it) }

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
internal fun AppPreferenceModule(i18next: I18next) = module {
    single<AppPreference> { BrowserAppPreference(localStorage, i18next) }
}
