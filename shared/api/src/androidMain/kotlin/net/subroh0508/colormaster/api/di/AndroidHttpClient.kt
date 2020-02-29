package net.subroh0508.colormaster.api.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.accept
import io.ktor.http.URLProtocol
import io.ktor.http.userAgent
import net.subroh0508.colormaster.api.BuildConfig
import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.internal.ContentType
import net.subroh0508.colormaster.api.internal.HOSTNAME
import net.subroh0508.colormaster.api.internal.ImasparqlApiClient
import net.subroh0508.colormaster.api.internal.UserAgent
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

internal actual val httpClient get() = HttpClient(OkHttp) {
    engine {
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(loggingInterceptor)
        }
    }
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = HOSTNAME
        }
        accept(ContentType.Application.SparqlJson)
        userAgent(UserAgent)
    }
    install(JsonFeature) {
        acceptContentTypes += ContentType.Application.SparqlJson
    }
}
