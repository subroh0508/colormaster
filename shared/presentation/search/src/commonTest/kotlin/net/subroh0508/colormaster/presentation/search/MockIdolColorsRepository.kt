package net.subroh0508.colormaster.presentation.search

import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.repository.IdolColorsRepository

class MockIdolColorsRepository : IdolColorsRepository {
    var expectedIdolName: IdolName? = null
    var expectedBrands: Brands? = null
    var expectedTypes: Set<Types> = setOf()
    var expectedLiveName: LiveName? = null
    var everyRand: (Int) -> List<IdolColor> = { listOf() }
    var everySearchByName: (IdolName?, Brands?, Set<Types>) -> List<IdolColor> = { _, _, _ -> listOf() }
    var everySearchByLive: (LiveName?) -> List<IdolColor> = { listOf() }

    override suspend fun favorite(id: String) = Unit
    override suspend fun unfavorite(id: String) = Unit
    override suspend fun getFavoriteIdolIds(): List<String> = listOf()
    override suspend fun rand(limit: Int): List<IdolColor> = everyRand(limit)
    override suspend fun search(ids: List<String>): List<IdolColor> = listOf()
    override suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>): List<IdolColor> {
        if (expectedIdolName == name && expectedBrands == brands && expectedTypes == types) {
            return everySearchByName(name, brands, types)
        }

        return listOf()
    }

    override suspend fun search(liveName: LiveName): List<IdolColor> {
        if (expectedLiveName == liveName) {
            return everySearchByLive(liveName)
        }

        return listOf()
    }
}

fun MockIdolColorsRepository.everyRand(block: (Int) -> List<IdolColor>) {
    everyRand = block
}

fun MockIdolColorsRepository.everySearch(
    expectIdolName: IdolName? = null,
    expectBrands: Brands? = null,
    expectTypes: Set<Types> = setOf(),
    block: (IdolName?, Brands?, Set<Types>) -> List<IdolColor>,
) {
    expectedIdolName = expectIdolName
    expectedBrands = expectBrands
    expectedTypes = expectTypes
    everySearchByName = block
}
