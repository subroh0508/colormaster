package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response
import net.subroh0508.colormaster.db.IdolColorsDatabase
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.query.ImasparqlQueries
import net.subroh0508.colormaster.repository.IdolColorsRepository

internal class IdolColorsRepositoryImpl(
    private val imasparqlClient: ImasparqlClient,
    private val database: IdolColorsDatabase,
    private val appPreference: AppPreference,
) : IdolColorsRepository {
    override suspend fun rand(limit: Int): List<IdolColor> = imasparqlClient.search(ImasparqlQueries.rand(appPreference.lang.code, limit)).toIdolColors()

    override suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>) =
        imasparqlClient.search(ImasparqlQueries.search(appPreference.lang.code, name, brands, types)).toIdolColors()

    override suspend fun search(ids: List<String>): List<IdolColor>  =
        imasparqlClient.search(ImasparqlQueries.search(appPreference.lang.code, ids)).toIdolColors()

    override suspend fun favorite(id: String) {

    }

    override suspend fun unfavorite(id: String) {

    }

    private fun Response<IdolColorJson>.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (idMap, nameMap, colorMap) ->
        val id = idMap["value"] ?: return@mapNotNull null
        val name = nameMap["value"] ?: return@mapNotNull null
        val color = colorMap["value"] ?: return@mapNotNull null

        IdolColor(id, name, HexColor(color))
    }
}
