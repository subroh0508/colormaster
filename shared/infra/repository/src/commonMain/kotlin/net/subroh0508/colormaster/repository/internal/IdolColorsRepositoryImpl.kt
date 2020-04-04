package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response
import net.subroh0508.colormaster.model.HexColor
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.query.ImasparqlQueries
import net.subroh0508.colormaster.repository.IdolColorsRepository

internal class IdolColorsRepositoryImpl(
    private val imasparqlClient: ImasparqlClient
) : IdolColorsRepository {
    override suspend fun rand(): List<IdolColor> = imasparqlClient.search(ImasparqlQueries.rand()).toIdolColors()

    override suspend fun search(name: IdolName?) = name?.let {
        imasparqlClient.search(ImasparqlQueries.searchByName(it)).toIdolColors()
    } ?: listOf()

    private fun Response<IdolColorJson>.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (sMap, nameMap, colorMap) ->
        val id = sMap["value"] ?: return@mapNotNull null
        val name = nameMap["value"] ?: return@mapNotNull null
        val color = colorMap["value"] ?: return@mapNotNull null

        IdolColor(id, name, HexColor(color))
    }
}
