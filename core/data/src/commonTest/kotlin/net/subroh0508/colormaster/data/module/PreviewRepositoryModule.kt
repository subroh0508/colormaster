package net.subroh0508.colormaster.data.module

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.data.di.MyIdolsRepositories
import net.subroh0508.colormaster.data.di.PreviewRepositories
import net.subroh0508.colormaster.model.MyIdolsRepository
import net.subroh0508.colormaster.model.PreviewRepository
import net.subroh0508.colormaster.network.auth.AuthClient
import net.subroh0508.colormaster.network.firestore.FirestoreClient
import net.subroh0508.colormaster.network.imasparql.di.Api
import net.subroh0508.colormaster.test.fake.FakeAuthClient
import net.subroh0508.colormaster.test.fake.FakeFirestoreClient
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal fun buildPreviewRepository(
    block: () -> HttpClient,
): PreviewRepository = koinApplication {
    modules(
        Api.Module(block()) + PreviewRepositories.Module
    )
}.koin.get(PreviewRepository::class)
