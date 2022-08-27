package net.subroh0508.colormaster.api.imasparql.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.Json
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.accept
import io.ktor.http.URLProtocol
import io.ktor.http.userAgent
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.api.imasparql.BuildConfig
import net.subroh0508.colormaster.api.imasparql.HOSTNAME
import net.subroh0508.colormaster.api.imasparql.internal.ContentType
import net.subroh0508.colormaster.api.imasparql.internal.UserAgent
import okhttp3.logging.HttpLoggingInterceptor

internal actual fun httpClient(json: Json) = HttpClient(OkHttp) {
    engine {
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(loggingInterceptor)
        }

        // @see https://github.com/ktorio/ktor/issues/1708
        config {
            retryOnConnectionFailure(true)
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
    Json {
        acceptContentTypes = listOf(ContentType.Application.SparqlJson)
        serializer = KotlinxSerializer(json)
    }
}
