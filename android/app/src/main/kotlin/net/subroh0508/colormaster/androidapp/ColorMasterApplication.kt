package net.subroh0508.colormaster.androidapp

import android.app.Application
import net.subroh0508.colormaster.repository.di.DataModule
import net.subroh0508.colormaster.components.core.koinApp
import org.koin.dsl.module

class ColorMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        koinApp.modules(
            DataModule + module {
                single<Application> { this@ColorMasterApplication }
            }
        )
    }
}
