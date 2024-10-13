package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.DefaultIdolColorsRepository
import net.subroh0508.colormaster.model.IdolColorsRepository
import org.koin.dsl.module

object IdolColorsRepositories {
    val Module get() = module {
        single<IdolColorsRepository> { DefaultIdolColorsRepository(get(), get(), get()) }
    }
}
