package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import net.subroh0508.colormaster.network.imasparql.ImasparqlClient
import net.subroh0508.colormaster.network.imasparql.json.LiveNameJson
import net.subroh0508.colormaster.network.imasparql.query.SuggestLiveQuery
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.model.LiveRepository
import net.subroh0508.colormaster.network.imasparql.serializer.Response

internal class DefaultLiveRepository(
    private val imasparqlClient: ImasparqlClient,
) : LiveRepository {
    private val names: MutableStateFlow<List<LiveName>> = MutableStateFlow(listOf())

    override fun getLiveNamesStream() = names.onStart {
        names.value = listOf()
    }

    override suspend fun suggest(
        dateRange: Pair<String, String>,
    ) = imasparqlClient.search(
        SuggestLiveQuery(dateRange = dateRange).build(),
        LiveNameJson.serializer(),
    ).toLiveNames().also { names.value = it }

    override suspend fun suggest(
        name: String?,
    ) = imasparqlClient.search(
        SuggestLiveQuery(name = name).build(),
        LiveNameJson.serializer(),
    ).toLiveNames().also { names.value = it }

    private fun Response<LiveNameJson>.toLiveNames() = results
        .bindings
        .mapNotNull { (liveNameMap) -> liveNameMap["value"]?.let { LiveName(it) } }
}
