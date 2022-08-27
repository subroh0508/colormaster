package net.subroh0508.colormaster.api.imasparql.internal

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.*
import io.ktor.utils.io.charsets.Charset
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.api.imasparql.ImasparqlClient
import net.subroh0508.colormaster.api.imasparql.serializer.Response

internal class ImasparqlApiClient(
    private val httpClient: HttpClient,
    private val json: Json,
) : ImasparqlClient {
    override suspend fun <T> search(
        query: String,
        serializer: KSerializer<T>,
    ): Response<T> {
        val response = httpClient.get(query)

        return json.decodeFromString(
            Response.serializer(serializer),
            response.bodyAsText(Charset.forName("UTF-8"))
        )
    }
}
