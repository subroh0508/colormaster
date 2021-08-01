package net.subroh0508.colormaster.presentation.myidols.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.common.ViewModel
import net.subroh0508.colormaster.presentation.myidols.model.MyIdolsUiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository

class MyIdolsViewModel(
    private val idolColorsRepository: IdolColorsRepository,
    coroutineScope: CoroutineScope? = null,
) : ViewModel(coroutineScope) {
    private val favorites: MutableStateFlow<List<IdolColor>> by lazy { MutableStateFlow(listOf()) }

    val uiModel: Flow<MyIdolsUiModel> by lazy {
        combine(favorites) { (favorites) -> MyIdolsUiModel(favorites = favorites) }
            .distinctUntilChanged().apply { launchIn(viewModelScope) }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            favorites.value = idolColorsRepository.search(
                idolColorsRepository.getFavoriteIdolIds(),
            )
        }
    }
}
