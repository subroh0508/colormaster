package net.subroh0508.colormaster.api.internal

import io.ktor.http.ContentType

abstract class ContentType {
    object Application {
        val SparqlJson = ContentType("application", "sparql-results+json")
    }
}
