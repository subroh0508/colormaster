package net.subroh0508.colormaster.data.mock

import io.ktor.client.engine.mock.*
import net.subroh0508.colormaster.network.imasparql.query.RandomQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByLiveQuery
import net.subroh0508.colormaster.network.imasparql.query.SearchByNameQuery
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.test.mockApi

fun mockRandom(
    lang: String,
    limit: Int,
    res: String,
) = mockApi { req ->
    if (req.url.parameters["query"] == RandomQuery(lang, limit).plainQuery) {
        return@mockApi respond(res, headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun mockSearchByName(
    lang: String,
    name: IdolName? = null,
    brands: Brands? = null,
    types: Set<Types> = setOf(),
    res: String,
) = mockApi { req ->
    val query = SearchByNameQuery(
        lang,
        name?.value,
        brands?.queryStr,
        types.map(Types::queryStr),
    ).plainQuery

    if (req.url.parameters["query"] == query) {
        return@mockApi respond(res, headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun mockSearchByLive(
    lang: String,
    liveName: LiveName?,
    res: String,
) = mockApi { req ->
    if (req.url.parameters["query"] == SearchByLiveQuery(lang, liveName?.value).plainQuery) {
        return@mockApi respond(res, headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun mockSearchById(
    lang: String,
    ids: List<String>,
    res: String,
) = mockApi { req ->
    if (req.url.parameters["query"] == SearchByIdQuery(lang, ids).plainQuery) {
        return@mockApi respond(res, headers = headers)
    }

    return@mockApi respondBadRequest()
}
