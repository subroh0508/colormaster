package net.subroh0508.colormaster.androidapp

import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.model.ui.commons.ThemeType
import org.koin.dsl.module

internal class AndroidAppPreference : AppPreference {
    override val lang get() = _lang
    override val theme get() = _themeType

    override fun setLanguage(lang: Languages) { _lang = lang }
    override fun setThemeType(type: ThemeType) { _themeType = type }

    private var _lang: Languages = Languages.JAPANESE
    private var _themeType: ThemeType = ThemeType.DAY
}

internal val AppPreferenceModule get() = module {
    single<AppPreference> { AndroidAppPreference() }
}
