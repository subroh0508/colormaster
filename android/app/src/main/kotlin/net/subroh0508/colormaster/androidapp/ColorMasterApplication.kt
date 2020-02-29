package net.subroh0508.colormaster.androidapp

import android.app.Application
import net.subroh0508.colormaster.corecomponent.AppModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

class ColorMasterApplication : Application(), KodeinAware {
    override val kodein by Kodein.lazy { import(AppModule) }
}
