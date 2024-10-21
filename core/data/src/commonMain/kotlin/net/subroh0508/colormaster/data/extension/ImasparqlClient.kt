package net.subroh0508.colormaster.data.extension

import net.subroh0508.colormaster.network.imasparql.ImasparqlClient
import net.subroh0508.colormaster.network.imasparql.json.IdolColorJson
import net.subroh0508.colormaster.network.imasparql.query.SearchByIdQuery

internal suspend fun ImasparqlClient.search(
    ids: List<String>,
    lang: String,
) = if (ids.isEmpty())
        listOf()
    else
        search(
            SearchByIdQuery(lang, ids).build(),
            IdolColorJson.serializer(),
        ).toIdolColors()
