package net.subroh0508.colormaster.data.mock

import io.ktor.client.engine.mock.*
import net.subroh0508.colormaster.network.imasparql.query.SuggestLiveQuery
import net.subroh0508.colormaster.test.mockHttpClient

fun mockSuggestLiveName(
    range: Pair<String, String>,
    res: String,
) = mockHttpClient { req ->
    if (req.url.parameters["query"] == SuggestLiveQuery(dateRange = range).plainQuery) {
        return@mockHttpClient respond(res, headers = headers)
    }

    return@mockHttpClient respondBadRequest()
}

fun mockSuggestLiveName(
    name: String?,
    res: String,
) = mockHttpClient { req ->
    if (req.url.parameters["query"] == SuggestLiveQuery(name = name).plainQuery) {
        return@mockHttpClient respond(res, headers = headers)
    }

    return@mockHttpClient respondBadRequest()
}
