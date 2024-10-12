package net.subroh0508.colormaster.data.mock

import io.ktor.client.engine.mock.*
import net.subroh0508.colormaster.network.imasparql.query.ImasparqlQuery
import net.subroh0508.colormaster.test.mockHttpClient

fun mockIdolSearch(
    vararg arg: Pair<ImasparqlQuery, String>,
) = mockHttpClient { req ->
    arg.forEach { (query, res) ->
        if (req.url.parameters["query"] == query.plainQuery) {
            return@mockHttpClient respond(res, headers = headers)
        }
    }

    return@mockHttpClient respondBadRequest()
}
