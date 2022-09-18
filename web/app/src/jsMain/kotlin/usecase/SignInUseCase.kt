package usecase

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.repository.AuthenticationRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

class SignInUseCase(
    private val isMobile: Boolean,
    private val setCurrentUserLoadState: (LoadState) -> Unit,
    private val repository: AuthenticationRepository,
    private val scope: CoroutineScope,
) {
    operator fun invoke() {
        if (isMobile) {
            scope.launch {
                runCatching { repository.signInWithGoogleForMobile() }
            }

            return
        }

        val job = scope.launch {
            runCatching { repository.signInWithGoogle() }
                .onSuccess { setCurrentUserLoadState(LoadState.Loaded(it)) }
                .onFailure { setCurrentUserLoadState(LoadState.Error(it)) }
        }

        setCurrentUserLoadState(LoadState.Loading)
        job.start()
    }
}

@Composable
fun rememberSignInUseCase(
    isMobile: Boolean,
    setCurrentUserLoadState: (LoadState) -> Unit,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): SignInUseCase {
    val scope = rememberCoroutineScope()
    val repository: AuthenticationRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return SignInUseCase(isMobile, setCurrentUserLoadState, repository, scope)
}
