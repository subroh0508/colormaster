package net.subroh0508.colormaster.androidapp

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.domain.valueobject.IdolColor
import net.subroh0508.colormaster.domain.valueobject.IdolName
import net.subroh0508.colormaster.repository.IdolColorsRepository

class MainViewModel(
    private val repository: IdolColorsRepository
) : ViewModel() {
    val items: List<IdolColor> get() = loadingState.value?.let { (it as? LoadingState.Loaded)?.items } ?: listOf()
    val loadingState: LiveData<LoadingState> get() = mutableLoadingState

    private val mutableLoadingState: MutableLiveData<LoadingState> = MutableLiveData(
        LoadingState.Loaded()
    )

    fun loadItems() {
        viewModelScope.launch {
            mutableLoadingState.postValue(
                LoadingState.Loading
            )

            runCatching { repository.search(IdolName("dummy")) }
                .onSuccess { mutableLoadingState.postValue(
                    LoadingState.Loaded(it)
                ) }
                .onFailure {
                    it.printStackTrace()
                    mutableLoadingState.postValue(
                        LoadingState.Error(it)
                    )
                }
        }
    }

    val itemCount get() = items.size
    fun itemId(position: Int) = items[position].id.hashCode().toLong()

    sealed class LoadingState {
        object Loading : LoadingState()
        data class Loaded(val items: List<IdolColor> = listOf()) : LoadingState()
        data class Error(val exception: Throwable) : LoadingState()
    }

    class Factory(
        private val repository: IdolColorsRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = MainViewModel(repository) as T
    }
}
