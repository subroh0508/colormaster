package net.subroh0508.colormaster.api.di

import io.ktor.client.HttpClient
import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.internal.ImasparqlApiClient
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

internal expect val httpClient: HttpClient

object Api {
    val Module get() = Kodein.Module(name = "ImasparqlClientModule") {
        bind<HttpClient>() with singleton { httpClient }
        bind<ImasparqlClient>() with singleton { ImasparqlApiClient(instance()) }
    }
}
