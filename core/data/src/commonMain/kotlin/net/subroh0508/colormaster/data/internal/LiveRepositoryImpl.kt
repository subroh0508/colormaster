package net.subroh0508.colormaster.data.internal

import net.subroh0508.colormaster.network.imasparql.ImasparqlClient
import net.subroh0508.colormaster.network.imasparql.json.LiveNameJson
import net.subroh0508.colormaster.network.imasparql.query.SuggestLiveQuery
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.data.LiveRepository

internal class LiveRepositoryImpl(
    private val imasparqlClient: ImasparqlClient,
) : LiveRepository {
    override suspend fun suggest(dateRange: Pair<String, String>) = imasparqlClient.search(
        SuggestLiveQuery(dateRange = dateRange).build(),
        LiveNameJson.serializer(),
    ).results.bindings.mapNotNull { (nameMap) -> LiveName(nameMap["value"] ?: return@mapNotNull null) }

    override suspend fun suggest(name: String?) = imasparqlClient.search(
        SuggestLiveQuery(name = name).build(),
        LiveNameJson.serializer(),
    ).results.bindings.mapNotNull { (nameMap) -> LiveName(nameMap["value"] ?: return@mapNotNull null) }
}
