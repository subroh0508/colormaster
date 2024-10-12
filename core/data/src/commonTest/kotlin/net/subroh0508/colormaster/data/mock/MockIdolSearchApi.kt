package net.subroh0508.colormaster.data.mock

import io.ktor.client.engine.mock.*
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import net.subroh0508.colormaster.data.IdolColorEmpty
import net.subroh0508.colormaster.network.imasparql.query.RandomQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByLiveQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByNameQuery
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.test.mockHttpClient

fun mockRandom(
    lang: String,
    limit: Int,
    res: String,
) = mockHttpClient { req ->
    mockRandomResponse(lang, limit, req, res) {
        respondBadRequest()
    }
}

fun mockSearchByName(
    lang: String,
    limit: Int,
    name: IdolName? = null,
    brands: Brands? = null,
    types: Set<Types> = setOf(),
    res: String,
) = mockHttpClient { req ->
    val query = SearchByNameQuery(
        lang,
        name?.value,
        brands?.queryStr,
        types.map(Types::queryStr),
    ).plainQuery

    mockRandomResponse(
        lang,
        limit,
        req,
        IdolColorEmpty,
    ) {
        if (req.url.parameters["query"] == query) {
            return@mockRandomResponse respond(res, headers = headers)
        }

        return@mockRandomResponse respondBadRequest()
    }
}

fun mockSearchByLive(
    lang: String,
    limit: Int,
    liveName: LiveName?,
    res: String,
) = mockHttpClient { req ->
    mockRandomResponse(
        lang,
        limit,
        req,
        IdolColorEmpty,
    ) {
        if (req.url.parameters["query"] == SearchByLiveQuery(lang, liveName?.value).plainQuery) {
            return@mockRandomResponse respond(res, headers = headers)
        }

        return@mockRandomResponse respondBadRequest()
    }
}

fun mockSearchById(
    lang: String,
    limit: Int,
    ids: List<String>,
    res: String,
) = mockHttpClient { req ->
    mockRandomResponse(
        lang,
        limit,
        req,
        IdolColorEmpty,
    ) {
        if (req.url.parameters["query"] == SearchByIdQuery(lang, ids).plainQuery) {
            return@mockRandomResponse respond(res, headers = headers)
        }

        return@mockRandomResponse respondBadRequest()
    }
}

suspend fun MockRequestHandleScope.mockRandomResponse(
    lang: String,
    limit: Int,
    req: HttpRequestData,
    res: String,
    handler: suspend MockRequestHandleScope.() -> HttpResponseData,
): HttpResponseData {
    if (req.url.parameters["query"] == RandomQuery(lang, limit).plainQuery) {
        return respond(res, headers = headers)
    }

    return handler()
}
