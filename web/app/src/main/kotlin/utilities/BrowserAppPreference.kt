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
) : AppPreference {
    override val lang get() = _lang
    override val themeType get() = paletteType

    override fun setLanguage(lang: Languages) { _lang = lang }
    override fun setThemeType(type: ThemeType) { paletteType = type }

    private var _lang: Languages = Languages.JAPANESE
    private var paletteType by localStorage
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

internal val AppPreferenceModule get() = module {
    single<AppPreference> { BrowserAppPreference(localStorage) }
}
