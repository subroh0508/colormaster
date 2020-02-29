package net.subroh0508.ktor.client.mpp.sample.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import net.subroh0508.ktor.client.mpp.sample.repository.json.Response
import net.subroh0508.ktor.client.mpp.sample.repository.mapper.toIdolColors

class IdolColorsRepository(
    private val httpClient: HttpClient
) {
    companion object {
        private const val HOSTNAME = "sparql.crssnky.xyz"
        private const val ENDPOINT = "/spql/imas/query"
    }

    suspend fun search() = httpClient.get<Response>(buildQuery()) {
        url {
            protocol = URLProtocol.HTTPS
            host = HOSTNAME
        }
    }.toIdolColors()

    private fun buildQuery(): String = buildString {
        append(ENDPOINT)
        append("?output=json")
        append("&query=")

        val query = """
            PREFIX imas: <https://sparql.crssnky.xyz/imasrdf/URIs/imas-schema.ttl#>
            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>

            SELECT * WHERE {?s rdfs:label ?name;imas:Color ?color;} order by rand()
        """.trimIndent()

        append(URLEncoder.encode(query))
    }
}
