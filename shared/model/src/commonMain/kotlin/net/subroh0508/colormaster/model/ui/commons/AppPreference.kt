package net.subroh0508.colormaster.model.ui.commons

import net.subroh0508.colormaster.model.Languages

interface AppPreference {
    val lang: Languages
    val theme: ThemeType
    fun setThemeType(type: ThemeType)
    interface State {
        val lang: Languages
        val theme: ThemeType

        operator fun component1(): Languages
        operator fun component2(): ThemeType
    }
}
