package net.subroh0508.colormaster.model

import net.subroh0508.colormaster.model.ui.idol.Filters

abstract class UiModel {
    data class Search(
        val items: List<IdolColorItem>,
        val filters: Filters,
        val error: Throwable?,
        val isLoading: Boolean = false
    ) {
        companion object {
            val INITIALIZED = Search(emptyList(), Filters.Empty, null)
        }

        val selected: List<IdolColor> get() = items.mapNotNull { it.takeIf(IdolColorItem::selected)?.idolColor }

        data class IdolColorItem(
            val idolColor: IdolColor,
            val selected: Boolean
        ) {
            constructor(idolColor: IdolColor) : this(idolColor, false)

            val itemId: Long get() = idolColor.id.hashCode().toLong()
        }
    }

    data class Penlight(
        val items: List<IdolColor>,
        val error: Throwable?,
        val isLoading: Boolean = false
    ) {
        companion object {
            val INITIALIZED = Penlight(emptyList(), null, true)
        }
    }
}
