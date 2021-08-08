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
    private val inCharges: MutableStateFlow<List<IdolColor>> by lazy { MutableStateFlow(listOf()) }
    private val favorites: MutableStateFlow<List<IdolColor>> by lazy { MutableStateFlow(listOf()) }
    private val selectedInCharges: MutableStateFlow<List<String>> by lazy { MutableStateFlow(listOf()) }
    private val selectedFavorites: MutableStateFlow<List<String>> by lazy { MutableStateFlow(listOf()) }

    val uiModel: StateFlow<MyIdolsUiModel> by lazy {
        combine(
            inCharges,
            favorites,
            selectedInCharges,
            selectedFavorites,
        ) { inCharges, favorites, selectedInCharges, selectedFavorites ->
            MyIdolsUiModel(inCharges, favorites, selectedInCharges, selectedFavorites)
        }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Eagerly, MyIdolsUiModel())
    }

    private val currentFavorites get() = uiModel.value.favorites
    private val currentInCharges get() = uiModel.value.inCharges

    fun loadInCharges() {
        viewModelScope.launch {
            inCharges.value = idolColorsRepository.search(
                idolColorsRepository.getInChargeOfIdolIds(),
            )
        }
    }
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
    fun selectInChargeOf(item: IdolColor, selected: Boolean) {
        selectedInCharges.value =
            if (selected)
                selectedInCharges.value + listOf(item.id)
            else
                selectedInCharges.value - listOf(item.id)
    }
    fun selectInChargeOfAll(selected: Boolean) {
        selectedInCharges.value =
            if (selected)
                currentInCharges.map(IdolColor::id)
            else
                listOf()
    }
}
