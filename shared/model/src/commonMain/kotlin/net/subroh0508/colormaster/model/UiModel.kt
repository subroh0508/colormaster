package net.subroh0508.colormaster.model

import net.subroh0508.colormaster.model.ui.idol.Filters

abstract class UiModel {
    data class Search(
        val items: List<IdolColor>,
        val idolName: IdolName?,
        val filters: Filters,
        val error: Throwable?,
        val isLoading: Boolean = false
    ) {
        companion object {
            val INITIALIZED  = Search(emptyList(), null, Filters.Empty, null)
        }

        val isConditionsEmpty get() = idolName == null && filters == Filters.Empty
    }
}
