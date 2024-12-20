package net.subroh0508.colormaster.features.search

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.common.CurrentLocalKoinApp
import net.subroh0508.colormaster.common.model.LoadState
import net.subroh0508.colormaster.common.ui.CurrentLocalLanguage
import net.subroh0508.colormaster.common.ui.Languages
import net.subroh0508.colormaster.features.search.model.SearchParams
import net.subroh0508.colormaster.model.IdolColorsRepository
import org.koin.core.KoinApplication

@Composable
fun rememberSearchIdolsUseCase(
    params: SearchParams,
    language: Languages = CurrentLocalLanguage(),
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): State<LoadState> {
    val scope = rememberCoroutineScope()
    val repository: IdolColorsRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return produceState<LoadState>(
        initialValue = LoadState.Initialize,
        params,
    ) {
        val job = scope.launch {
            runCatching { repository.search(params, language) }
                .onSuccess { value = LoadState.Loaded(it) }
                .onFailure { value = LoadState.Error(it) }
        }

        value = LoadState.Loading
        job.start()
    }
}

private suspend fun IdolColorsRepository.search(
    params: SearchParams?,
    language: Languages,
) = when (params) {
    is SearchParams.ByName -> params.takeUnless { it.isEmpty() }?.let {
        search(it.idolName, it.brands, it.types, language.code)
    } ?: rand(10, language.code)
    is SearchParams.ByLive -> params.liveName?.let {
        search(it, language.code)
    } ?: listOf()
    else -> listOf()
}
