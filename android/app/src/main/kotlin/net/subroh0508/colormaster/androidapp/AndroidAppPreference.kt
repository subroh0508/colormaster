package net.subroh0508.colormaster.androidapp

import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton


internal class AndroidAppPreference : AppPreference {
    override val lang get() = _lang

    override fun setLanguage(lang: Languages) { _lang = lang }

    private var _lang: Languages = Languages.JAPANESE
}

internal val AppPreferenceModule get() = DI.Module(name = "AndroidAppPreference") {
    bind<AppPreference>( ) with singleton { AndroidAppPreference() }
}
