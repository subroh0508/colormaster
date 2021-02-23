package net.subroh0508.colormaster.presentation.search.model

import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.common.LoadState

sealed class SearchUiModel {
    open val items: List<IdolColorListItem> = listOf()
    open val error: Throwable? = null
    open val isLoading: Boolean = false

    abstract val params: SearchParams

    val searchState get() = when {
        error != null -> SearchState.ERROR
        isLoading -> SearchState.WAITING
        params.isEmpty() -> SearchState.RANDOM
        else -> SearchState.SEARCHED
    }

    val selectedItems: List<IdolColor>
        get() = items.filter(IdolColorListItem::selected)
            .map { (id, name, hexColor) -> IdolColor(id, name.value, hexColor) }

    companion object {
        operator fun invoke(
            loadState: LoadState,
            selected: List<String>,
            favorites: List<String>,
        ) = Favorites(
            loadState.getIdolColorListItems(selected, favorites),
            loadState.getErrorOrNull(),
            loadState.isLoading,
        )

        operator fun invoke(
            params: SearchParams.ByName,
            loadState: LoadState,
            selected: List<String>,
            favorites: List<String>,
        ) = ByName(
            loadState.getIdolColorListItems(selected, favorites),
            params,
            loadState.getErrorOrNull(),
            loadState.isLoading,
        )

        operator fun invoke(
            params: SearchParams.ByLive,
            idolColorLoadState: LoadState,
            liveLoadState: LoadState,
            selected: List<String>,
            favorites: List<String>,
        ) = ByLive(
            idolColorLoadState.getIdolColorListItems(selected, favorites),
            params.suggests(liveLoadState.getValueOrNull() ?: listOf()),
            idolColorLoadState.getErrorOrNull() ?: liveLoadState.getErrorOrNull(),
            idolColorLoadState.isLoading || liveLoadState.isLoading,
        )

        private fun LoadState.getIdolColorListItems(
            selected: List<String>,
            favorites: List<String>,
        ) = getValueOrNull<List<IdolColor>>()
            ?.map { IdolColorListItem(it, selected.contains(it.id), favorites.contains(it.id)) }
            ?: listOf()
    }

    data class Favorites(
        override val items: List<IdolColorListItem>,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false,
    ) : SearchUiModel() {
        override val params = SearchParams.None

        companion object {
            val INITIALIZED = Favorites(listOf())
        }
    }

    data class ByName(
        override val items: List<IdolColorListItem>,
        override val params: SearchParams,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false,
    ) : SearchUiModel() {
        companion object {
            val INITIALIZED = ByName(listOf(), SearchParams.ByName.EMPTY)
        }
    }

    data class ByLive(
        override val items: List<IdolColorListItem>,
        override val params: SearchParams,
        override val error: Throwable? = null,
        override val isLoading: Boolean = false,
    ) : SearchUiModel() {
        companion object {
            val INITIALIZED = ByLive(listOf(), SearchParams.ByLive.EMPTY)
        }
    }
}

expect enum class SearchByTab {
    BY_NAME, BY_LIVE
}
