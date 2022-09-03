package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.LiveRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

@Composable
fun rememberSearchLiveUseCase(
    params: SearchParams.ByLive?,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): State<LoadState> {
    val scope = rememberCoroutineScope()
    val repository: LiveRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return produceState<LoadState>(
        initialValue = LoadState.Initialize,
        params,
    ) {
        if (params == null) {
            value = LoadState.Initialize
            return@produceState
        }
        val (_, query, date) = params

        val job = scope.launch {
            runCatching {
                when {
                    date != null -> date.range()?.let { repository.suggest(it) }
                    query != null -> repository.suggest(query)
                    else -> null
                } ?: listOf()
            }
                .onSuccess { value = LoadState.Loaded(it) }
                .onFailure { value = LoadState.Error(it) }
        }

        value = LoadState.Loading
        job.start()
    }
}