package net.subroh0508.colormaster.data.mock

import io.ktor.client.engine.mock.*
import net.subroh0508.colormaster.data.LiveNameDateRange
import net.subroh0508.colormaster.data.LiveNameTitle
import net.subroh0508.colormaster.network.imasparql.query.SuggestLiveQuery
import net.subroh0508.colormaster.test.mockApi

fun mockSuggestLiveName(
    range: Pair<String, String>,
    res: String,
) = mockApi { req ->
    if (req.url.parameters["query"] == SuggestLiveQuery(dateRange = range).plainQuery) {
        return@mockApi respond(res, headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun mockSuggestLiveName(
    name: String?,
    res: String,
) = mockApi { req ->
    if (req.url.parameters["query"] == SuggestLiveQuery(name = name).plainQuery) {
        return@mockApi respond(res, headers = headers)
    }

    return@mockApi respondBadRequest()
}
