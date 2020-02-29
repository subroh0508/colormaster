package net.subroh0508.colormaster.query

import net.subroh0508.colormaster.domain.valueobject.IdolName
import net.subroh0508.colormaster.query.internal.URLEncoder

object ImasparqlQueries {
    fun search(name: IdolName) = buildQuery("""
        SELECT * WHERE {?s rdfs:label ?name;imas:Color ?color;} order by rand()
    """.trimIndent())

    private fun buildQuery(query: String) = buildString {
        append(ENDPOINT_MAIN)
        append("?output=json")
        append("&query=")

        val plainQuery = buildString {
            append(PREFIX_IMAS)
            append(PREFIX_RDFS)

            append(query)
        }

        append(URLEncoder.encode(plainQuery))
    }

    private const val ENDPOINT_MAIN = "/spql/imas/query"

    private const val PREFIX_IMAS = "PREFIX imas: <https://sparql.crssnky.xyz/imasrdf/URIs/imas-schema.ttl#>"
    private const val PREFIX_RDFS = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
}
