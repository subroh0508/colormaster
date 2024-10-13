package net.subroh0508.colormaster.model

import kotlinx.coroutines.flow.Flow

interface IdolColorsRepository {
    fun idols(limit: Int, lang: String): Flow<List<IdolColor>>

    fun inChargeOfIdolIds(): Flow<List<String>>

    fun favoriteIdolIds(): Flow<List<String>>

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
