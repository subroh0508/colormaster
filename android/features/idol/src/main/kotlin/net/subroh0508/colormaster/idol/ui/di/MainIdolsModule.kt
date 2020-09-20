package net.subroh0508.colormaster.idol.ui.di

import net.subroh0508.colormaster.idol.ui.viewmodel.IdolsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val MainIdolsModule = module {
    viewModel { IdolsViewModel(get()) }
}
