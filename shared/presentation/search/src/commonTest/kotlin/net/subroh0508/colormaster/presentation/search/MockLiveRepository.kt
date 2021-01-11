package net.subroh0508.colormaster.presentation.search

import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.repository.LiveRepository

class MockLiveRepository : LiveRepository {
    var expectedDateRange: Pair<String, String>? = null
    var expectedName: String? = null
    var everySuggestionsByDateRange: (Pair<String, String>) -> List<LiveName> = { listOf() }
    var everySuggestionsByName: (String?) -> List<LiveName> = { listOf() }

    override suspend fun suggest(dateRange: Pair<String, String>): List<LiveName> {
        if (expectedDateRange?.first == dateRange.first && expectedDateRange?.second == dateRange.second) {
            return everySuggestionsByDateRange(dateRange)
        }

        return listOf()
    }

    override suspend fun suggest(name: String?): List<LiveName> {
        if (expectedName == name) {
            return everySuggestionsByName(name)
        }

        return listOf()
    }
}

fun MockLiveRepository.everySuggest(
    dateRange: Pair<String, String>,
    block: (Pair<String, String>) -> List<LiveName>,
) {
    expectedDateRange = dateRange
    everySuggestionsByDateRange = block
}

fun MockLiveRepository.everySuggest(
    name: String?,
    block: (String?) -> List<LiveName>,
) {
    expectedName = name
    everySuggestionsByName= block
}
