package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.Flow
import net.subroh0508.colormaster.model.LiveName

interface LiveRepository {
    fun names(): Flow<List<LiveName>>

    suspend fun suggest(dateRange: Pair<String, String>): List<LiveName>

    suspend fun suggest(name: String?): List<LiveName>
}
