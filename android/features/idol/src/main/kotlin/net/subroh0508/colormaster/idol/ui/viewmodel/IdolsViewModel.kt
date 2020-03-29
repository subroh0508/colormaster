package net.subroh0508.colormaster.idol.ui.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.extensions.combine
import net.subroh0508.colormaster.extensions.requireValue
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.model.ui.commons.LoadState
import net.subroh0508.colormaster.model.ui.idol.Filters
import net.subroh0508.colormaster.repository.IdolColorsRepository

class IdolsViewModel(
    private val repository: IdolColorsRepository
) : ViewModel() {
    data class UiModel(
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val items: List<IdolColor> = listOf(),
        val idolName: IdolName? = null,
        val filters: Filters = Filters.Empty
    )

    val items: List<IdolColor> get() = uiModel.value?.items ?: listOf()

    private val idolsLoadStateLiveData: MutableLiveData<LoadState<List<IdolColor>>> = MutableLiveData(LoadState.Loaded(listOf()))
    private val idolNameLiveData: MutableLiveData<IdolName> = MutableLiveData()
    private val filterLiveData: MutableLiveData<Filters> = MutableLiveData(Filters.Empty)

    val uiModel: LiveData<UiModel> = combine(
        UiModel(),
        liveData1 = idolsLoadStateLiveData,
        liveData2 = filterLiveData,
        liveData3 = idolNameLiveData
    ) { uiModel, loadState, filters, idolName ->

        UiModel(
            loadState.isLoading,
            loadState.getErrorOrNull(),
            loadState.getValueOrNull() ?: uiModel.items,
            idolName,
            filters
        )
    }

    fun loadItems() {
        viewModelScope.launch {
            idolsLoadStateLiveData.postValue(LoadState.Loading)

            runCatching { repository.search(IdolName("dummy")) }
                .onSuccess { idolsLoadStateLiveData.postValue(LoadState.Loaded(it)) }
                .onFailure {
                    it.printStackTrace()
                    idolsLoadStateLiveData.postValue(LoadState.Error(it))
                }
        }
    }

    fun filterChanged(title: Titles, checked: Boolean) {
        filterLiveData.value = if (checked) Filters(title) else Filters.Empty
    }

    fun filterChanged(type: Types, checked: Boolean) {
        val filters = filterLiveData.requireValue()

        filterLiveData.value = if (checked) filters + type else filters - type
    }

    fun filterChanged(unitName: UnitName?) {
        val filters = filterLiveData.requireValue()

        filterLiveData.value = filters.assign(unitName)
    }

    val itemCount get() = items.size
    fun itemId(position: Int) = items[position].id.hashCode().toLong()
}
