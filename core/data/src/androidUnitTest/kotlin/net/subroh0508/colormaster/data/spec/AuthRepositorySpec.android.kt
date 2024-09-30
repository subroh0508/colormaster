package net.subroh0508.colormaster.data.spec

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import net.subroh0508.colormaster.data.AuthRepository
import net.subroh0508.colormaster.data.data.fromGoogle
import net.subroh0508.colormaster.data.di.AuthRepositories
import net.subroh0508.colormaster.data.fake.FakeAuthClient
import net.subroh0508.colormaster.model.auth.CredentialProvider
import net.subroh0508.colormaster.model.auth.CurrentUser
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.test.extension.flowToList
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class AuthRepositorySpec : DescribeSpec({
    val currentUser = CurrentUser(
        fromGoogle.uid,
        listOf(CredentialProvider.Google(fromGoogle.providers.first().email ?: "")),
    )

    it("#currentUser: it should return current user") {
        val repository = buildRepository()

        val (instances, _) = flowToList(repository.currentUser())

        runBlocking {
            repository.signInWithGoogle("idToken")
            repository.signOut()

            instances.let {
                it should haveSize(3)
                it should containExactly(
                    null,
                    currentUser,
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
