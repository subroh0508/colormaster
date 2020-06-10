package net.subroh0508.colormaster.model.ui.commons

import net.subroh0508.colormaster.model.Languages

interface AppPreference {
    val lang: Languages

    fun setLanguage(lang: Languages)
}
