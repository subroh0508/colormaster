package net.subroh0508.colormaster.api.di

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.internal.ImasparqlApiClient
import org.koin.dsl.module

internal expect val httpClient: HttpClient

object Api {
    val Module get() = module {
        single { httpClient }
        single<ImasparqlClient> { ImasparqlApiClient(get()) }
    }
}
