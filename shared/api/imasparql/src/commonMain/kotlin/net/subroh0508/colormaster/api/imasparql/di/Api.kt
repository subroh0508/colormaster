package net.subroh0508.colormaster.api.imasparql.di

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.api.imasparql.ImasparqlClient
import net.subroh0508.colormaster.api.imasparql.internal.ImasparqlApiClient
import org.koin.dsl.module

internal expect val httpClient: HttpClient

object Api {
    @Suppress("FunctionName")
    fun Module(client: HttpClient = httpClient) = module {
        single { client }
        single<ImasparqlClient> { ImasparqlApiClient(get()) }
    }
}
