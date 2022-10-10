package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.components.core.CurrentLocalKoinApp
import net.subroh0508.colormaster.components.core.model.LoadState
import net.subroh0508.colormaster.components.core.ui.CurrentLocalLanguage
import net.subroh0508.colormaster.components.core.ui.Languages
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinApplication

@Composable
fun rememberFetchInChargeIdolsUseCase(
    isSignedIn: Boolean,
    language: Languages = CurrentLocalLanguage(),
    koinApp: KoinApplication = CurrentLocalKoinApp(),
) = rememberFetchMyIdolsUseCase(isSignedIn, language, koinApp, fetchIds = IdolColorsRepository::getInChargeOfIdolIds)

@Composable
fun rememberFetchFavoriteIdolsUseCase(
    isSignedIn: Boolean,
    language: Languages = CurrentLocalLanguage(),
    koinApp: KoinApplication = CurrentLocalKoinApp(),
) = rememberFetchMyIdolsUseCase(isSignedIn, language, koinApp, fetchIds = IdolColorsRepository::getFavoriteIdolIds)

@Composable
private fun rememberFetchMyIdolsUseCase(
    isSignedIn: Boolean,
    language: Languages,
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
                repository.search(repository.fetchIds(), language.code)
            }
                .onSuccess { loadState.value = LoadState.Loaded(it) }
                .onFailure { loadState.value = LoadState.Error(it) }
        }

        loadState.value = LoadState.Loading
        job.start()
    }

    return loadState
}
