package net.subroh0508.ktor.client.mpp.sample.androidapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import kotlinx.coroutines.launch
import net.subroh0508.ktor.client.mpp.sample.repository.IdolColorsRepository
import net.subroh0508.ktor.client.mpp.sample.valueobject.IdolColor
import okhttp3.logging.HttpLoggingInterceptor

class MainViewModel : ViewModel() {
    val items: List<IdolColor> get() = loadingState.value?.let { (it as? LoadingState.Loaded)?.items } ?: listOf()
    val loadingState: LiveData<LoadingState> get() = mutableLoadingState

    private val mutableLoadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.Loaded())

    fun loadItems() {
        viewModelScope.launch {
            mutableLoadingState.postValue(LoadingState.Loading)

            runCatching { repository.search() }
                .onSuccess { mutableLoadingState.postValue(LoadingState.Loaded(it)) }
                .onFailure {
                    it.printStackTrace()
                    mutableLoadingState.postValue(LoadingState.Error(it))
                }
        }
    }

    val itemCount get() = items.size
    fun itemId(position: Int) = items[position].id.hashCode().toLong()

    private val httpClient: HttpClient = HttpClient(OkHttp) {
        engine {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(loggingInterceptor)
            }
        }
        install(JsonFeature)
    }
    private val repository: IdolColorsRepository by lazy { IdolColorsRepository(httpClient) }

    sealed class LoadingState {
        object Loading : LoadingState()
        data class Loaded(val items: List<IdolColor> = listOf()) : LoadingState()
        data class Error(val exception: Throwable) : LoadingState()
    }
}
