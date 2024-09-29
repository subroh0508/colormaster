package net.subroh0508.colormaster.network.imasparql.internal

import io.ktor.http.ContentType

abstract class ContentType {
    object Application {
        val SparqlJson = ContentType("application", "sparql-results+json")
    }
}
