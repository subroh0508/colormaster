package net.subroh0508.colormaster.androidapp

import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.koin.dsl.module

internal class AndroidAppPreference : AppPreference {
    override val lang get() = _lang

    override fun setLanguage(lang: Languages) { _lang = lang }

    private var _lang: Languages = Languages.JAPANESE
}

internal val AppPreferenceModule get() = module {
    single<AppPreference> { AndroidAppPreference() }
}
