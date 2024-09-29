package net.subroh0508.colormaster.repository.mock

import io.ktor.http.*
import net.subroh0508.colormaster.network.imasparql.internal.ContentType

internal val headers = headersOf(
    "Content-Type" to listOf(ContentType.Application.SparqlJson.toString())
)
