package net.subroh0508.colormaster.androidapp

import android.app.Application
import net.subroh0508.colormaster.components.core.AppModule
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import net.subroh0508.colormaster.presentation.search.viewmodel.FavoritesViewModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ColorMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(AppModule + AppPreferenceModule + module {
                single<Application> { this@ColorMasterApplication }
                viewModel { SearchByNameViewModel(get()) }
                viewModel { FavoritesViewModel(get()) }
                viewModel { PreviewViewModel(get()) }
            })
        }
    }
}
