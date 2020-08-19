package net.subroh0508.colormaster.androidapp

import android.app.Application
import net.subroh0508.colormaster.components.core.AppModule
import org.kodein.di.DI
import org.kodein.di.DIAware

class ColorMasterApplication : Application(), DIAware {
    override val di by DI.lazy {
        import(AppModule)
        import(AppPreferenceModule)
    }
}
