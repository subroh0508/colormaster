package net.subroh0508.colormaster.presentation.preview

import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.repository.IdolColorsRepository

class MockIdolColorsRepository : IdolColorsRepository {
    lateinit var responseOfSearch: (List<String>) -> List<IdolColor>

    override suspend fun favorite(id: String) = Unit
    override suspend fun unfavorite(id: String) = Unit
    override suspend fun getFavoriteIdolIds(): List<String> = listOf()
    override suspend fun rand(limit: Int): List<IdolColor> = listOf()
    override suspend fun search(ids: List<String>): List<IdolColor> = responseOfSearch(ids)
    override suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>): List<IdolColor> = listOf()
}
