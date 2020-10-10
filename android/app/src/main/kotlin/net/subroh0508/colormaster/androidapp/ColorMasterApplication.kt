package net.subroh0508.colormaster.androidapp

import android.app.Application
import net.subroh0508.colormaster.androidapp.viewmodel.IdolSearchViewModel
import net.subroh0508.colormaster.components.core.AppModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ColorMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(AppModule + AppPreferenceModule + module {
                viewModel { IdolSearchViewModel(get()) }
            })
        }
    }
}
