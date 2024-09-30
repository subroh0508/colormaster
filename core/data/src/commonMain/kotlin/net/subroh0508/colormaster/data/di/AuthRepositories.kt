package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.AuthRepository
import net.subroh0508.colormaster.data.internal.AuthRepositoryImpl
import org.koin.dsl.module

object AuthRepositories {
    val Module get() = module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
    }
}
