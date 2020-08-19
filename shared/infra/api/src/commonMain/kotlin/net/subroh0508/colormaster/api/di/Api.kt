package net.subroh0508.colormaster.api.di

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.internal.ImasparqlApiClient
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

internal expect val httpClient: HttpClient

object Api {
    val Module get() = DI.Module(name = "ImasparqlClientModule") {
        bind<HttpClient>() with singleton { httpClient }
        bind<ImasparqlClient>() with singleton { ImasparqlApiClient(instance()) }
    }
}
