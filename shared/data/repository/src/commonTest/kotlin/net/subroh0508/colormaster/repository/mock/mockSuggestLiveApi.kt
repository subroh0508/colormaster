package net.subroh0508.colormaster.repository.mock

import io.ktor.client.engine.mock.*
import net.subroh0508.colormaster.api.imasparql.query.SuggestLiveQuery
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.test.mockApi
import net.subroh0508.colormaster.test.resultJson

fun mockSuggestLiveName(
    range: Pair<String, String>,
    res: List<LiveName>,
) = mockApi { req ->
    if (req.url.parameters["query"] == SuggestLiveQuery(dateRange = range).plainQuery) {
        return@mockApi respond(toJson(res), headers = headers)
    }

    return@mockApi respondBadRequest()
}

fun mockSuggestLiveName(
    name: String?,
    res: List<LiveName>,
) = mockApi { req ->
    if (req.url.parameters["query"] == SuggestLiveQuery(name = name).plainQuery) {
        return@mockApi respond(toJson(res), headers = headers)
    }

    return@mockApi respondBadRequest()
}


private fun toJson(res: List<LiveName>) = resultJson {
    res.joinToString(",") { "{\"name\":{\"type\":\"literal\",\"value\":\"${it.value}\"}}" }
}
