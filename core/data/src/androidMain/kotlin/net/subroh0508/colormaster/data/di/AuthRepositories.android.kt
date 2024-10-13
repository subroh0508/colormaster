package net.subroh0508.colormaster.data.di

import net.subroh0508.colormaster.data.internal.AuthRepositoryImpl
import net.subroh0508.colormaster.model.auth.AuthRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual object AuthRepositories {
    actual val Module: Module = module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
    }
}
