package net.subroh0508.colormaster.data.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import net.subroh0508.colormaster.data.AuthRepository
import net.subroh0508.colormaster.data.di.AuthRepositories
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.test.model.GoogleUser
import net.subroh0508.colormaster.test.extension.flowToList
import net.subroh0508.colormaster.test.fake.FakeAuthClient
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class AuthRepositorySpec : FunSpec({
    test("#currentUser: it should return current user") {
        val repository = buildRepository()

        val (instances, _) = flowToList(repository.currentUser())

        repository.signInWithGoogle()
        repository.signOut()
        repository.signInWithGoogleForMobile()
        repository.signOut()

        instances.let {
            it should haveSize(5)
            it should containExactly(
                null,
                GoogleUser,
                null,
                GoogleUser,
                null,
            )
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
