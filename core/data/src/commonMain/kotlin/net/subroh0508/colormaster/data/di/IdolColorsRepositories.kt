package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.IdolColorsRepository
import net.subroh0508.colormaster.data.internal.IdolColorsRepositoryImpl
import org.koin.dsl.module

object IdolColorsRepositories {
    val Module get() = module {
        single<IdolColorsRepository> { IdolColorsRepositoryImpl(get(), get(), get()) }
    }
}
