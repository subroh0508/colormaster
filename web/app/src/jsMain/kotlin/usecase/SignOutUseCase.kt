package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.repository.AuthenticationRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

class SignOutUseCase(
    private val setCurrentUserLoadState: (LoadState) -> Unit,
    private val repository: AuthenticationRepository,
    private val scope: CoroutineScope,
) {
    operator fun invoke() {
        val job = scope.launch(start = CoroutineStart.LAZY) {
            runCatching { repository.signOut() }
                .onSuccess { setCurrentUserLoadState(LoadState.Loaded<CurrentUser?>(null)) }
                .onFailure { setCurrentUserLoadState(LoadState.Error(it)) }
        }

        setCurrentUserLoadState(LoadState.Loading)
        job.start()
    }
}

@Composable
fun rememberSignOutUseCase(
    setCurrentUserLoadState: (LoadState) -> Unit,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): SignOutUseCase {
    val scope = rememberCoroutineScope()
    val repository: AuthenticationRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return SignOutUseCase(setCurrentUserLoadState, repository, scope)
}
