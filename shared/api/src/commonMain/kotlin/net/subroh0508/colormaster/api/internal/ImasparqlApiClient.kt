package net.subroh0508.colormaster.api.internal

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response
import net.subroh0508.colormaster.query.ImasparqlQueries

internal class ImasparqlApiClient(
    private val httpClient: HttpClient
) : ImasparqlClient {
    override suspend fun search(
        name: String
    ) = httpClient.get<Response<IdolColorJson>>(ImasparqlQueries.searchByName(name))
}
