package net.subroh0508.colormaster.api.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.Json
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.accept
import io.ktor.http.URLProtocol
import io.ktor.http.userAgent
import net.subroh0508.colormaster.api.internal.ContentType
import net.subroh0508.colormaster.api.internal.HOSTNAME
import net.subroh0508.colormaster.api.internal.UserAgent

internal actual val httpClient get() = HttpClient(Js) {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = HOSTNAME
        }
        accept(ContentType.Application.SparqlJson)
        userAgent(UserAgent)
    }
    Json {
        acceptContentTypes += ContentType.Application.SparqlJson
        serializer = KotlinxSerializer(kotlinx.serialization.json.Json.nonstrict)
    }
}
