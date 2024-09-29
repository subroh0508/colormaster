package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.AuthenticationRepository
import net.subroh0508.colormaster.data.internal.AuthenticationRepositoryImpl
import org.koin.dsl.module

object AuthenticationRepositories {
    val Module get() = module {
        single<AuthenticationRepository> { AuthenticationRepositoryImpl(get()) }
    }
}
