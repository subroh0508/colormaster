package net.subroh0508.colormaster.data.module

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.data.di.LiveRepositories
import net.subroh0508.colormaster.model.LiveRepository
import net.subroh0508.colormaster.network.imasparql.di.Api
import org.koin.dsl.koinApplication

internal fun buildLiveRepository(
    block: () -> HttpClient,
): LiveRepository = koinApplication {
    modules(
        Api.Module(block()) + LiveRepositories.Module
    )
}.koin.get(LiveRepository::class)
