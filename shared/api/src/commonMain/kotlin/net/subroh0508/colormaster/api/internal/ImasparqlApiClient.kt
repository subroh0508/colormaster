package net.subroh0508.colormaster.api.internal

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.json.Response

internal class ImasparqlApiClient(
    private val httpClient: HttpClient
) : ImasparqlClient {
    override suspend fun <T> search(query: String) = httpClient.get<Response<T>>(query)
}
