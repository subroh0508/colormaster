package net.subroh0508.colormaster.model.ui.commons

import net.subroh0508.colormaster.model.Languages

interface AppPreference {
    val lang: Languages
    val themeType: ThemeType?

    fun setLanguage(lang: Languages)
    fun setThemeType(type: ThemeType)
    fun toggleThemeType()
}
