package utilities

import net.subroh0508.colormaster.model.Languages
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal class BrowserAppPreference : AppPreference {
    override val lang get() = _lang

    override fun setLanguage(lang: Languages) { _lang = lang }

    private var _lang: Languages = Languages.JAPANESE
}

internal val AppPreferenceModule get() = DI.Module(name = "BrowserAppPreference") {
    bind<AppPreference>() with singleton { BrowserAppPreference() }
}
