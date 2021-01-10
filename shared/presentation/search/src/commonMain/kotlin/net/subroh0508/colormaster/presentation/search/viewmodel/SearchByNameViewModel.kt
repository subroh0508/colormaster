package net.subroh0508.colormaster.presentation.search.viewmodel

import kotlinx.coroutines.*
import net.subroh0508.colormaster.repository.IdolColorsRepository

class SearchByNameViewModel(
    repository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : SearchViewModel(repository, coroutineScope)
