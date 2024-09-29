package net.subroh0508.colormaster.data

import net.subroh0508.colormaster.model.*

interface IdolColorsRepository {
    suspend fun rand(limit: Int, lang: String): List<IdolColor>

    suspend fun search(name: IdolName?, brands: Brands?, types: Set<Types>, lang: String): List<IdolColor>

    suspend fun search(liveName: LiveName, lang: String): List<IdolColor>

    suspend fun search(ids: List<String>, lang: String): List<IdolColor>

    suspend fun getInChargeOfIdolIds(): List<String>

    suspend fun getFavoriteIdolIds(): List<String>

    suspend fun registerInChargeOf(id: String)

    suspend fun unregisterInChargeOf(id: String)

    suspend fun favorite(id: String)

    suspend fun unfavorite(id: String)
}
