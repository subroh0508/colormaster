package net.subroh0508.colormaster.features.myidols

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.common.CurrentLocalKoinApp
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinApplication

class AddIdolToFavoriteUseCase(
    private val repository: IdolColorsRepository,
    private val scope: CoroutineScope,
    private val favorites: MutableState<List<String>> = mutableStateOf(listOf()),
) : State<List<String>> by favorites {
    init {
        scope.launch {
            runCatching {
                repository.getFavoriteIdolIds()
            }
                .onSuccess { favorites.value = it }
                .onFailure { favorites.value = listOf() }
        }
    }

    fun add(id: String, favorite: Boolean) {
        scope.launch {
            runCatching {
                if (favorite)
                    repository.favorite(id)
                else
                    repository.unfavorite(id)
                repository.getFavoriteIdolIds()
            }
                .onSuccess { favorites.value = it }
                .onFailure { favorites.value = listOf() }
        }
    }
}

@Composable
fun rememberAddIdolToFavoriteUseCase(
    isSignedIn: Boolean,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): AddIdolToFavoriteUseCase {
    val scope = rememberCoroutineScope()

    return remember(koinApp, isSignedIn) { AddIdolToFavoriteUseCase(koinApp.koin.get(), scope) }
}
