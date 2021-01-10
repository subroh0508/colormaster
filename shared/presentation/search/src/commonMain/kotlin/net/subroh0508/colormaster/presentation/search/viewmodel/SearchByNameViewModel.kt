package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.*
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.IdolColorsRepository

class SearchByNameViewModel(
    repository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : SearchViewModel<SearchParams.ByName>(repository, SearchParams.ByName.EMPTY, coroutineScope) {
    override fun search() =
        if (searchParams.isEmpty())
            loadRandom()
        else
            super.search()

    override suspend fun search(params: SearchParams.ByName) = idolColorsRepository.search(
        params.idolName, params.brands, params.types,
    )
}
