package net.subroh0508.colormaster.idol.ui.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.extensions.combine
import net.subroh0508.colormaster.extensions.requireValue
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.model.UiModel.Search.IdolColorItem
import net.subroh0508.colormaster.model.ui.commons.LoadState
import net.subroh0508.colormaster.model.ui.idol.Filters
import net.subroh0508.colormaster.repository.IdolColorsRepository

class IdolsViewModel(
    private val repository: IdolColorsRepository
) : ViewModel() {
    val items: List<IdolColorItem> get() = uiModel.value?.items ?: listOf()

    private val idolsLoadStateLiveData: MutableLiveData<LoadState<List<IdolColor>>> = MutableLiveData(LoadState.Loaded(listOf()))
    private val filterLiveData: MutableLiveData<Filters> = MutableLiveData(Filters.Empty)

    val uiModel: LiveData<UiModel.Search> = combine(
        UiModel.Search.INITIALIZED,
        liveData1 = idolsLoadStateLiveData,
        liveData2 = filterLiveData
    ) { uiModel, loadState, filters ->

        UiModel.Search(
            loadState.getValueOrNull()?.map(::IdolColorItem) ?: uiModel.items,
            filters,
            loadState.getErrorOrNull(),
            loadState.isLoading
        )
    }

    fun loadItems() {
        viewModelScope.launch {
            idolsLoadStateLiveData.postValue(LoadState.Loading)

            runCatching { repository.search(filterLiveData.value?.idolName, filterLiveData.value?.title, filterLiveData.value?.types ?: setOf()) }
                .onSuccess { idolsLoadStateLiveData.postValue(LoadState.Loaded(it)) }
                .onFailure {
                    it.printStackTrace()
                    idolsLoadStateLiveData.postValue(LoadState.Error(it))
                }
        }
    }

    fun filterChanged(title: Titles, checked: Boolean) {
        val filters = filterLiveData.requireValue()

        filterLiveData.value = if (checked) Filters(filters.idolName, title) else Filters.Empty

        loadItems()
    }

    fun filterChanged(type: Types, checked: Boolean) {
        val filters = filterLiveData.requireValue()

        filterLiveData.value = if (checked) filters + type else filters - type
    }

    fun filterChanged(idolName: IdolName?) {
        val filters = filterLiveData.requireValue()

        filterLiveData.value = Filters(idolName, filters.title, filters.types)

        loadItems()
    }

    fun filterChanged(unitName: UnitName?) {
        val filters = filterLiveData.requireValue()

        filterLiveData.value = filters.assign(unitName)
    }

    val itemCount get() = items.size
    fun itemId(position: Int) = items[position].itemId
}
