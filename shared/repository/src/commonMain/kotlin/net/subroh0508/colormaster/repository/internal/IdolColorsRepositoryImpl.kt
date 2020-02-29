package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response
import net.subroh0508.colormaster.domain.valueobject.IdolColor
import net.subroh0508.colormaster.domain.valueobject.IdolName
import net.subroh0508.colormaster.repository.IdolColorsRepository

internal class IdolColorsRepositoryImpl(
    private val imasparqlClient: ImasparqlClient
) : IdolColorsRepository {
    override suspend fun search(name: IdolName) = imasparqlClient.search(name.value).toIdolColors()

    private fun Response<IdolColorJson>.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (sMap, nameMap, colorMap) ->
        val id = sMap["value"] ?: return@mapNotNull null
        val name = nameMap["value"] ?: return@mapNotNull null
        val color = colorMap["value"] ?: return@mapNotNull null

        IdolColor(id, name, color)
    }

}
