package net.subroh0508.colormaster.query

import net.subroh0508.colormaster.query.internal.URLEncoder

abstract class ImasparqlQuery {
    companion object {
        private const val ENDPOINT_MAIN = "/spql/imas/query"

        private const val PREFIX_SCHEMA = "PREFIX schema: <http://schema.org/>"
        private const val PREFIX_IMAS = "PREFIX imas: <https://sparql.crssnky.xyz/imasrdf/URIs/imas-schema.ttl#>"
        private const val PREFIX_RDF = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
        private const val PREFIX_RDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
    }

    protected abstract val rawQuery: String

    val plainQuery get() = buildString {
        append(PREFIX_SCHEMA)
        append(PREFIX_IMAS)
        append(PREFIX_RDF)
        append(PREFIX_RDFS)

        append(rawQuery)
    }

    fun build() = buildString {
        append(ENDPOINT_MAIN)
        append("?output=json")
        append("&query=")

        append(URLEncoder.encode(plainQuery))
    }

    protected fun String.trimIndentAndBr() = trimIndent().replace("[\n\r]", "")
}
