package net.subroh0508.colormaster.androidapp

import android.app.Application
import net.subroh0508.colormaster.common.koinApp
import net.subroh0508.colormaster.data.di.DataModule
import org.koin.dsl.module

class ColorMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        koinApp.modules(
            DataModule +
                module {
                    single<Application> { this@ColorMasterApplication }
                },
        )
    }
}
