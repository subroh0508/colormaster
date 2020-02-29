package net.subroh0508.colormaster.api

import net.subroh0508.colormaster.api.serializer.Response

interface ImasparqlClient {
    suspend fun <T> search(query: String): Response<T>
}
