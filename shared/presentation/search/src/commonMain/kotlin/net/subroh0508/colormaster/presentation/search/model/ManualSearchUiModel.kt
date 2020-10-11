package net.subroh0508.colormaster.presentation.search.model

import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.ui.idol.SearchState
import net.subroh0508.colormaster.utilities.LoadState

class ManualSearchUiModel internal constructor(
    val items: List<IdolColorListItem>,
    val params: SearchParams,
    val error: Throwable? = null,
    val isLoading: Boolean = false,
) {
    companion object {
        val INITIALIZED = ManualSearchUiModel(listOf(), SearchParams.EMPTY)

        operator fun invoke(params: SearchParams, loadState: LoadState) = ManualSearchUiModel(
            loadState.getValueOrNull<List<IdolColor>>()?.map(::IdolColorListItem) ?: listOf(),
            params,
            loadState.getErrorOrNull(),
            loadState.isLoading,
        )
    }

    val searchState get() = when {
        error != null -> SearchState.ERROR
        isLoading -> SearchState.WAITING
        params == SearchParams.EMPTY -> SearchState.RANDOM
        else -> SearchState.SEARCHED
    }

    val selectedItems: List<IdolColor>
        get() = items.filter(IdolColorListItem::selected).map { (id, name, hexColor) -> IdolColor(id, name.value, hexColor) }
}
