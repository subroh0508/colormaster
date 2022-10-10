package net.subroh0508.colormaster.repository.di

import net.subroh0508.colormaster.repository.LiveRepository
import net.subroh0508.colormaster.repository.internal.LiveRepositoryImpl
import org.koin.dsl.module

object LiveRepositories {
    val Module get() = module {
        single<LiveRepository> { LiveRepositoryImpl(get()) }
    }
}
