package net.subroh0508.colormaster.data

import net.subroh0508.colormaster.model.LiveName

interface LiveRepository {
    suspend fun suggest(dateRange: Pair<String, String>): List<LiveName>

    suspend fun suggest(name: String?): List<LiveName>
}
