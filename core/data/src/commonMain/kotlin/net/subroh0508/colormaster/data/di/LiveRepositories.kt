package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.LiveRepository
import net.subroh0508.colormaster.data.internal.LiveRepositoryImpl
import org.koin.dsl.module

object LiveRepositories {
    val Module get() = module {
        single<LiveRepository> { LiveRepositoryImpl(get()) }
    }
}
