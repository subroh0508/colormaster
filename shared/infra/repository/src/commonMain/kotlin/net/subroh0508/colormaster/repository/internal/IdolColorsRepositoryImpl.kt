package net.subroh0508.colormaster.repository.internal

import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.api.json.IdolColorJson
import net.subroh0508.colormaster.api.serializer.Response
import net.subroh0508.colormaster.db.IdolColorsDatabase
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import net.subroh0508.colormaster.query.RandomQuery
import net.subroh0508.colormaster.query.SearchByIdQuery
import net.subroh0508.colormaster.query.SearchByNameQuery
import net.subroh0508.colormaster.repository.IdolColorsRepository

internal class IdolColorsRepositoryImpl(
    private val imasparqlClient: ImasparqlClient,
    private val database: IdolColorsDatabase,
    private val appPreference: AppPreference,
) : IdolColorsRepository {
    override suspend fun rand(limit: Int) =
        imasparqlClient.search(RandomQuery(appPreference.lang.code, limit).build()).toIdolColors()

    override suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>) =
        imasparqlClient.search(SearchByNameQuery(appPreference.lang.code, name, brands, types).build()).toIdolColors()

    override suspend fun search(ids: List<String>)  =
        imasparqlClient.search(SearchByIdQuery(appPreference.lang.code, ids).build()).toIdolColors()

    override suspend fun getFavoriteIdolIds(): List<String> = database.getFavorites().toList()

    override suspend fun favorite(id: String) = database.addFavorite(id)

    override suspend fun unfavorite(id: String) = database.removeFavorite(id)

    private fun Response<IdolColorJson>.toIdolColors(): List<IdolColor> = results.bindings.mapNotNull { (idMap, nameMap, colorMap) ->
        val id = idMap["value"] ?: return@mapNotNull null
        val name = nameMap["value"] ?: return@mapNotNull null
        val color = colorMap["value"] ?: return@mapNotNull null

        IdolColor(id, name, HexColor(color))
    }
}
