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
    private val managed: MutableStateFlow<List<IdolColor>> by lazy { MutableStateFlow(listOf()) }
    private val favorites: MutableStateFlow<List<IdolColor>> by lazy { MutableStateFlow(listOf()) }
    private val selectedManaged: MutableStateFlow<List<String>> by lazy { MutableStateFlow(listOf()) }
    private val selectedFavorites: MutableStateFlow<List<String>> by lazy { MutableStateFlow(listOf()) }

    val uiModel: StateFlow<MyIdolsUiModel> by lazy {
        combine(
            managed,
            favorites,
            selectedManaged,
            selectedFavorites,
        ) { managed, favorites, selectedManaged, selectedFavorites ->
            MyIdolsUiModel(managed, favorites, selectedManaged, selectedFavorites)
        }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Eagerly, MyIdolsUiModel())
    }

    private val currentFavorites get() = uiModel.value.favorites
    private val currentManaged get() = uiModel.value.managed

    fun loadFavorites() {
        viewModelScope.launch {
            favorites.value = idolColorsRepository.search(
                idolColorsRepository.getFavoriteIdolIds(),
            )
        }
    }

    fun selectFavorite(item: IdolColor, selected: Boolean) {
        selectedFavorites.value =
            if (selected)
                selectedFavorites.value + listOf(item.id)
            else
                selectedFavorites.value - listOf(item.id)
    }
    fun selectFavoritesAll(selected: Boolean) {
        selectedFavorites.value =
            if (selected)
                currentFavorites.map(IdolColor::id)
            else
                listOf()
    }
    fun selectManaged(item: IdolColor, selected: Boolean) {
        selectedManaged.value =
            if (selected)
                selectedManaged.value + listOf(item.id)
            else
                selectedManaged.value - listOf(item.id)
    }
    fun selectManagedAll(selected: Boolean) {
        selectedManaged.value =
            if (selected)
                currentManaged.map(IdolColor::id)
            else
                listOf()
    }
}
