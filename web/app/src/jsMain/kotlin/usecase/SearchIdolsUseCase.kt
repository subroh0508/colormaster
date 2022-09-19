package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.search.model.IdolColorList
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

@Composable
fun rememberSearchIdolsUseCase(
    params: SearchParams?,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): State<LoadState> {
    val scope = rememberCoroutineScope()
    val repository: IdolColorsRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return produceState<LoadState>(
        initialValue = LoadState.Initialize,
        params,
    ) {
        val job = scope.launch {
            runCatching { repository.search(params) }
                .onSuccess { value = LoadState.Loaded(it) }
                .onFailure { value = LoadState.Error(it) }
        }

        value = LoadState.Loading
        job.start()
    }
}

private suspend fun IdolColorsRepository.search(
    params: SearchParams?,
) = when (params) {
    is SearchParams.ByName -> params.takeUnless { it.isEmpty() }?.let {
        search(it.idolName, it.brands, it.types)
    } ?: rand(10)
    is SearchParams.ByLive -> params.liveName?.let {
        search(it)
    } ?: listOf()
    else -> listOf()
}
