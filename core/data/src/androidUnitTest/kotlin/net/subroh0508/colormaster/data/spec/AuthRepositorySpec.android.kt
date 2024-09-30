package net.subroh0508.colormaster.data.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import net.subroh0508.colormaster.data.module.buildAuthRepository
import net.subroh0508.colormaster.test.extension.flowToList
import net.subroh0508.colormaster.test.model.GoogleUser

class AuthRepositorySpec : FunSpec({
    test("#currentUser: it should return current user") {
        val repository = buildAuthRepository()

        val (instances, _) = flowToList(repository.currentUser())

        repository.signInWithGoogle("idToken")
        repository.signOut()

        instances.let {
            it should haveSize(3)
            it should containExactly(
                null,
                GoogleUser,
                null,
            )
        }
    }
})
