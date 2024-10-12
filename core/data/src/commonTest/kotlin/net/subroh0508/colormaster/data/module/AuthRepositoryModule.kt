package net.subroh0508.colormaster.data.module

import net.subroh0508.colormaster.data.AuthRepository
import net.subroh0508.colormaster.data.di.AuthRepositories
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.test.fake.FakeAuthClient
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal fun buildAuthRepository(): AuthRepository = koinApplication {
    modules(
        module {
            single<AuthClient> { FakeAuthClient() }
        } + AuthRepositories.Module,
    )
}.koin.get(AuthRepository::class)
