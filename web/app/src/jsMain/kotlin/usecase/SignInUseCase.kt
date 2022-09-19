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
    private val repository: AuthenticationRepository,
    private val scope: CoroutineScope,
) {
    operator fun invoke() {
        scope.launch {
            runCatching {
                if (isMobile)
                    repository.signInWithGoogleForMobile()
                else
                    repository.signInWithGoogle()
            }
        }
    }
}

@Composable
fun rememberSignInUseCase(
    isMobile: Boolean,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): SignInUseCase {
    val scope = rememberCoroutineScope()
    val repository: AuthenticationRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return SignInUseCase(isMobile, repository, scope)
}
