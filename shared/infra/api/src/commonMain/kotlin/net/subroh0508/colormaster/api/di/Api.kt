package net.subroh0508.colormaster.api.di

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.internal.ImasparqlApiClient
import org.koin.dsl.module

internal expect val httpClient: HttpClient

object Api {
    @Suppress("FunctionName")
    fun Module(client: HttpClient = httpClient) = module {
        single { client }
        single<ImasparqlClient> { ImasparqlApiClient(get()) }
    }
}
