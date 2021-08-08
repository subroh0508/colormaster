package net.subroh0508.colormaster.repository.di

import net.subroh0508.colormaster.repository.IdolColorsRepository
import net.subroh0508.colormaster.repository.internal.IdolColorsRepositoryImpl
import org.koin.dsl.module

object IdolColorsRepositories {
    val Module get() = module {
        single<IdolColorsRepository> { IdolColorsRepositoryImpl(get(), get(), get(), get()) }
    }
}
