package net.subroh0508.colormaster.network.imasparql.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.*
import io.ktor.client.request.accept
import io.ktor.http.URLProtocol
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.network.imasparql.HOSTNAME
import net.subroh0508.colormaster.network.imasparql.internal.ContentType

internal actual fun httpClient(json: Json) = HttpClient(Js) {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = HOSTNAME
        }
        accept(ContentType.Application.SparqlJson)
    }
    Json(json) {}
}
