package net.subroh0508.colormaster.features.search.model

actual enum class SearchByTab(
    val query: String,
    val labelKey: String,
) {
    BY_NAME("", "searchBox.tabs.name"),
    BY_LIVE("live", "searchBox.tabs.live");

    companion object {
        fun findByQuery(query: String?) = values().find { it.query == query } ?: BY_NAME
    }
}
