package net.subroh0508.colormaster.data.module

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.data.di.MyIdolsRepositories
import net.subroh0508.colormaster.model.MyIdolsRepository
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.network.imasparql.di.Api
import net.subroh0508.colormaster.test.fake.FakeAuthClient
import net.subroh0508.colormaster.test.fake.FakeFirestoreClient
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal fun buildMyIdolsRepository(
    block: () -> HttpClient,
): Triple<MyIdolsRepository, AuthClient, FirestoreClient> {
    val authClient: AuthClient = FakeAuthClient()
    val firestoreClient: FirestoreClient = FakeFirestoreClient()

    val repository: MyIdolsRepository = koinApplication {
        modules(
            Api.Module(block()) + module {
                single { authClient }
                single { firestoreClient }
            } + MyIdolsRepositories.Module
        )
    }.koin.get(MyIdolsRepository::class)

    return Triple(repository, authClient, firestoreClient)
}
