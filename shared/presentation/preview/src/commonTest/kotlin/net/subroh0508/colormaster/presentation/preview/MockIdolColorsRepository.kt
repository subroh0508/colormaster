package net.subroh0508.colormaster.presentation.preview

import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.repository.IdolColorsRepository

class MockIdolColorsRepository : IdolColorsRepository {
    lateinit var expectedIds: List<String>
    lateinit var everySearch: (List<String>) -> List<IdolColor>

    override suspend fun registerInChargeOf(id: String) = Unit
    override suspend fun unregisterInChargeOf(id: String) = Unit
    override suspend fun favorite(id: String) = Unit
    override suspend fun unfavorite(id: String) = Unit
    override suspend fun getInChargeOfIdolIds(): List<String> = listOf()
    override suspend fun getFavoriteIdolIds(): List<String> = listOf()
    override suspend fun rand(limit: Int, lang: String): List<IdolColor> = listOf()
    override suspend fun search(ids: List<String>, lang: String): List<IdolColor> = if (expectedIds == ids) everySearch(ids) else listOf()
    override suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>, lang: String): List<IdolColor> = listOf()
    override suspend fun search(liveName: LiveName, lang: String): List<IdolColor> = listOf()
}

fun MockIdolColorsRepository.mockSearch(expectIds: List<String>, block: (List<String>) -> List<IdolColor>) {
    expectedIds = expectIds
    everySearch = block
}
