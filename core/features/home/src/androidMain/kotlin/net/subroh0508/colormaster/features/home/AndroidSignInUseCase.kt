package net.subroh0508.colormaster.features.home

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.repository.AuthenticationRepository
import org.koin.core.KoinApplication

actual class SignInUseCase(
    private val repository: AuthenticationRepository,
    private val scope: CoroutineScope,
) {
    operator fun invoke(idToken: String) {
        scope.launch {
            runCatching {
                repository.signInWithGoogle(idToken)
            }
        }
    }
}

@Composable
actual fun rememberSignInUseCase(
    koinApp: KoinApplication,
): SignInUseCase {
    val scope = rememberCoroutineScope()
    val repository: AuthenticationRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return remember(koinApp) { SignInUseCase(repository, scope) }
}
