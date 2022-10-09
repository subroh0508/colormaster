package net.subroh0508.colormaster.api.imasparql.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.api.imasparql.ImasparqlClient
import net.subroh0508.colormaster.api.imasparql.internal.ImasparqlApiClient
import org.koin.dsl.module

internal expect fun httpClient(json: Json): HttpClient

object Api {
    private val json by lazy {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = true
        }
    }

    @Suppress("FunctionName")
    fun Module(client: HttpClient = httpClient(json)) = module {
        single { client }
        single<ImasparqlClient> { ImasparqlApiClient(get(), json) }
    }
}
