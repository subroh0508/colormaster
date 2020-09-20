package net.subroh0508.colormaster.androidapp

import android.app.Application
import net.subroh0508.colormaster.components.core.AppModule
import net.subroh0508.colormaster.idol.ui.di.MainIdolsModule
import org.koin.core.context.startKoin

class ColorMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(AppModule + AppPreferenceModule + MainIdolsModule)
        }
    }
}
