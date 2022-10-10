package net.subroh0508.colormaster.api.imasparql

import kotlinx.serialization.KSerializer
import net.subroh0508.colormaster.api.imasparql.serializer.Response

interface ImasparqlClient {
    suspend fun <T> search(
        query: String,
        serializer: KSerializer<T>,
    ): Response<T>
}
