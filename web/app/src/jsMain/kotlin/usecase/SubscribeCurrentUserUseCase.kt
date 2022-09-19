package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.repository.AuthenticationRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

@Composable
fun rememberSubscribeCurrentUserUseCase(
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): State<CurrentUser?> {
    val scope = rememberCoroutineScope()
    val repository: AuthenticationRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return produceState<CurrentUser?>(
        initialValue = null,
    ) {
        repository.subscribe().onEach {
            value = it
        }.launchIn(scope)
    }
}
