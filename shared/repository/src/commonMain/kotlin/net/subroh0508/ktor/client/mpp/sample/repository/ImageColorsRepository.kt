package net.subroh0508.ktor.client.mpp.sample.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import net.subroh0508.ktor.client.mpp.sample.repository.json.Response
import net.subroh0508.ktor.client.mpp.sample.valueobject.ImageColor

class ImageColorsRepository(
    private val httpClient: HttpClient
) {
    companion object {
        private const val BASE_URL = "https://sparql.crssnky.xyz"
        private const val ENDPOINT = "/spql/imas/query"
    }

    suspend fun search(): List<ImageColor> {
        val response = httpClient.get<Response>(buildQuery()) {
            accept(ContentType("application", "sparql-results+json"))
        }

        return response.results.bindings.mapNotNull { (sMap, nameMap, colorMap) ->
            val id = sMap["value"] ?: return@mapNotNull null
            val name = nameMap["value"] ?: return@mapNotNull null
            val color = colorMap["value"] ?: return@mapNotNull null

            ImageColor(id, name, color)
        }
    }

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
