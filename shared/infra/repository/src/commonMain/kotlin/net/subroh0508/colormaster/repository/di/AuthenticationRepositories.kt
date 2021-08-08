package net.subroh0508.colormaster.repository.di

import net.subroh0508.colormaster.repository.AuthenticationRepository
import net.subroh0508.colormaster.repository.internal.AuthenticationRepositoryImpl
import org.koin.dsl.module

object AuthenticationRepositories {
    val Module get() = module {
        single<AuthenticationRepository> { AuthenticationRepositoryImpl(get()) }
    }
}
