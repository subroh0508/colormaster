package net.subroh0508.colormaster.data.spec

import io.kotest.common.runBlocking
import io.kotest.common.runPromise
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import net.subroh0508.colormaster.data.AuthRepository
import net.subroh0508.colormaster.data.data.fromGoogle
import net.subroh0508.colormaster.data.di.AuthRepositories
import net.subroh0508.colormaster.data.fake.FakeAuthClient
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.test.extension.flowToList
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class AuthRepositorySpec : DescribeSpec({
    it("#currentUser: it should return current user") {
        val repository = buildRepository()

        val (instances, _) = flowToList(repository.currentUser())

        runPromise {
            repository.signInWithGoogle()
            repository.signOut()
            repository.signInWithGoogleForMobile()
            repository.signOut()

            instances.let {
                it should haveSize(5)
                it should containExactly(
                    null,
                    fromGoogle,
                    null,
                    fromGoogle,
                    null,
                )
            }
        }
    }
})

private fun buildRepository(): AuthRepository = koinApplication {
    modules(
        module {
            single<AuthClient> { FakeAuthClient() }
        } + AuthRepositories.Module,
    )
}.koin.get(AuthRepository::class)
