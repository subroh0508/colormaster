package net.subroh0508.colormaster.data.module

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.data.di.PreviewRepositories
import net.subroh0508.colormaster.model.PreviewRepository
import net.subroh0508.colormaster.network.imasparql.di.Api
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal fun buildPreviewRepository(
    block: () -> HttpClient,
): PreviewRepository =
    koinApplication {
        modules(
            Api.Module(block()) + PreviewRepositories.Module,
        )
    }.koin.get(PreviewRepository::class)
