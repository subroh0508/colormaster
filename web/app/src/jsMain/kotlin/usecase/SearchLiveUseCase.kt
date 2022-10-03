package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.search.model.LiveNameQuery
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.LiveRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

@Composable
fun rememberSearchLiveUseCase(
    liveNameQuery: LiveNameQuery,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): State<LoadState> {
    val scope = rememberCoroutineScope()
    val repository: LiveRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return produceState<LoadState>(
        initialValue = LoadState.Initialize,
        liveNameQuery,
    ) {
        val (query, isSettled) = liveNameQuery

        if (query == null) {
            value = LoadState.Initialize
            return@produceState
        }

        if (isSettled) {
            value = LoadState.Loaded(listOf<LiveName>())
            return@produceState
        }

        val job = scope.launch {
            runCatching {
                when {
                    liveNameQuery.isNumber() -> liveNameQuery.toDateNum()?.range()?.let { repository.suggest(it) }
                    liveNameQuery.query != null -> repository.suggest(liveNameQuery.query)
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