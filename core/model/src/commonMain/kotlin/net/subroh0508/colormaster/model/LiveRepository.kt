package net.subroh0508.colormaster.model

import kotlinx.coroutines.flow.Flow

interface LiveRepository {
    fun getLiveNamesStream(): Flow<List<LiveName>>

    suspend fun suggest(dateRange: Pair<String, String>): List<LiveName>

    suspend fun suggest(name: String?): List<LiveName>
}
