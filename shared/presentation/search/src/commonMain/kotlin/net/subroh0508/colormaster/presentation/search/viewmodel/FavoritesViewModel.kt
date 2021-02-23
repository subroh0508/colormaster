package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository

class FavoritesViewModel(
    repository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : SearchViewModel<SearchParams.None>(repository, SearchParams.None, coroutineScope) {
    @ExperimentalCoroutinesApi
    override val uiModel: Flow<SearchUiModel>
        get() = combine(
            idolsLoadState,
            selected,
            favorites,
        ) { loadState, selected, favorites ->
            SearchUiModel(loadState, selected, favorites)
        }.distinctUntilChanged()

    override fun search() {
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            runCatching {
                val ids = idolColorsRepository.getFavoriteIdolIds()
                ids to idolColorsRepository.search(ids)
            }
                .onSuccess { (favoriteIds, idols) ->
                    favorites.value = favoriteIds
                    idolsLoadState.value = LoadState.Loaded(idols)
                }
                .onFailure {
                    favorites.value = listOf()
                    idolsLoadState.value = LoadState.Error(it)
                }
        }

        idolsLoadState.value = LoadState.Loading
        job.start()
    }

    override suspend fun search(params: SearchParams.None): List<IdolColor> = listOf()
}
