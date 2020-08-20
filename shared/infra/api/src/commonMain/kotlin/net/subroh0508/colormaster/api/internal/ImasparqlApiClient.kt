package net.subroh0508.colormaster.api.internal

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.utils.io.charsets.Charset
import kotlinx.serialization.json.Json
import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response

internal class ImasparqlApiClient(
    private val httpClient: HttpClient
) : ImasparqlClient {
    override suspend fun search(query: String): Response<IdolColorJson> {
        val response = httpClient.get<HttpResponse>(query)

        return Json {
            isLenient = true
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = true
        }.decodeFromString(
            Response.serializer(IdolColorJson.serializer()),
            response.readText(Charset.forName("UTF-8"))
        )
    }
}
