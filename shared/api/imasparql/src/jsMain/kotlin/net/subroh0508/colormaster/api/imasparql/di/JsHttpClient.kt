package net.subroh0508.colormaster.api.imasparql.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.*
import io.ktor.client.features.json.Json
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.accept
import io.ktor.http.URLProtocol
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.api.imasparql.HOSTNAME
import net.subroh0508.colormaster.api.imasparql.internal.ContentType

internal actual fun httpClient(json: Json) = HttpClient(Js) {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = HOSTNAME
        }
        accept(ContentType.Application.SparqlJson)
    }
    Json {
        acceptContentTypes = listOf(ContentType.Application.SparqlJson)
        serializer = KotlinxSerializer(json)
    }
}
