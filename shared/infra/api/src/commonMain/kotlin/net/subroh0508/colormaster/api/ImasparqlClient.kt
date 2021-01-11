package net.subroh0508.colormaster.api

import kotlinx.serialization.KSerializer
import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response

interface ImasparqlClient {
    suspend fun <T> search(
        query: String,
        serializer: KSerializer<T>,
    ): Response<T>
}
