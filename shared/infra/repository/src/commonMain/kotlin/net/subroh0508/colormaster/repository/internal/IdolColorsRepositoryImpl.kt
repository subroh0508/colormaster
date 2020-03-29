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
    override suspend fun search(name: IdolName) =
        imasparqlClient.search(ImasparqlQueries.searchByName(name)).toIdolColors()

    private fun Response<IdolColorJson>.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (sMap, nameMap, colorMap) ->
        val id = sMap["value"] ?: return@mapNotNull null
        val name = nameMap["value"] ?: return@mapNotNull null
        val color = colorMap["value"] ?: return@mapNotNull null

        IdolColor(id, name, HexColor(color))
    }
}
