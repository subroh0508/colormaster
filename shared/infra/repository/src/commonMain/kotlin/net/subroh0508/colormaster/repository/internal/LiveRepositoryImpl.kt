package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.json.LiveNameJson
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.query.SuggestLiveQuery
import net.subroh0508.colormaster.repository.LiveRepository

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
