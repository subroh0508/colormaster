package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

@Composable
fun rememberFetchInChargeIdolsUseCase(
    isSignedIn: Boolean,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
) = rememberFetchMyIdolsUseCase(isSignedIn, koinApp, fetchIds = IdolColorsRepository::getInChargeOfIdolIds)

@Composable
fun rememberFetchFavoriteIdolsUseCase(
    isSignedIn: Boolean,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
) = rememberFetchMyIdolsUseCase(isSignedIn, koinApp, fetchIds = IdolColorsRepository::getFavoriteIdolIds)

@Composable
private fun rememberFetchMyIdolsUseCase(
    isSignedIn: Boolean,
    koinApp: KoinApplication,
    fetchIds: suspend IdolColorsRepository.() -> List<String>,
): State<LoadState> {
    val repository: IdolColorsRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    val loadState = remember(koinApp) { mutableStateOf<LoadState>(LoadState.Initialize) }

    LaunchedEffect(koinApp, isSignedIn) {
        if (!isSignedIn) {
            return@LaunchedEffect
        }

        val job = launch(start = CoroutineStart.LAZY) {
            runCatching {
                repository.search(repository.fetchIds())
            }
                .onSuccess { loadState.value = LoadState.Loaded(it) }
                .onFailure { loadState.value = LoadState.Error(it) }
        }

        loadState.value = LoadState.Loading
        job.start()
    }

    return loadState
}
