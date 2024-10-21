package net.subroh0508.colormaster.model

import kotlinx.coroutines.flow.Flow

interface MyIdolsRepository {
    fun getInChargeOfIdolsStream(lang: String): Flow<List<IdolColor>>

    fun getFavoriteIdolsStream(lang: String): Flow<List<IdolColor>>
}
