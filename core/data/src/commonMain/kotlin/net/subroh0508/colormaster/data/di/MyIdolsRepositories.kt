package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.DefaultMyIdolsRepository
import net.subroh0508.colormaster.model.MyIdolsRepository
import org.koin.dsl.module

object MyIdolsRepositories {
    val Module get() = module {
        single<MyIdolsRepository> { DefaultMyIdolsRepository(get(), get(), get()) }
    }
}
