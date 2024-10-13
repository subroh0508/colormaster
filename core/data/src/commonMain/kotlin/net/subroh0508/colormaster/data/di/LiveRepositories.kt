package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.DefaultLiveRepository
import net.subroh0508.colormaster.model.LiveRepository
import org.koin.dsl.module

object LiveRepositories {
    val Module get() = module {
        single<LiveRepository> { DefaultLiveRepository(get()) }
    }
}
