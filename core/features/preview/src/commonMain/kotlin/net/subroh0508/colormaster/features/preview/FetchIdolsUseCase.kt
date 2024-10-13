package net.subroh0508.colormaster.features.preview

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.common.CurrentLocalKoinApp
import net.subroh0508.colormaster.common.model.LoadState
import net.subroh0508.colormaster.common.ui.CurrentLocalLanguage
import net.subroh0508.colormaster.common.ui.Languages
import net.subroh0508.colormaster.model.IdolColorsRepository
import org.koin.core.KoinApplication

@Composable
fun rememberFetchIdolsUseCase(
    ids: List<String>,
    language: Languages = CurrentLocalLanguage(),
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): State<LoadState> {
    val scope = rememberCoroutineScope()
    val repository: IdolColorsRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return produceState<LoadState>(
        initialValue = LoadState.Initialize,
        ids,
    ) {
        if (ids.isEmpty()) {
            value = LoadState.Error(EmptyIdsRequestException())
            return@produceState
        }

        val job = scope.launch {
            runCatching { repository.search(ids, language.code) }
                .onSuccess { value = LoadState.Loaded(it) }
                .onFailure { value = LoadState.Error(it) }
        }

        value = LoadState.Loading
        job.start()
    }
}

class EmptyIdsRequestException : Throwable()
