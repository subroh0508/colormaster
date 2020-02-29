package net.subroh0508.colormaster.api

import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response

interface ImasparqlClient {
    suspend fun search(name: String): Response<IdolColorJson>
}
