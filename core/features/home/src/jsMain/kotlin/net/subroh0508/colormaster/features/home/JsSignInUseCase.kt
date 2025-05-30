package net.subroh0508.colormaster.features.home

import androidx.compose.runtime.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.auth.AuthRepository
import org.koin.core.KoinApplication

actual class SignInUseCase(
    private val isMobile: Boolean,
    private val repository: AuthRepository,
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
actual fun rememberSignInUseCase(
    koinApp: KoinApplication,
): SignInUseCase {
    val scope = rememberCoroutineScope()
    val repository: AuthRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return remember(koinApp) { SignInUseCase(isMobile, repository, scope) }
}

private val isMobile: Boolean get() = """(iPhone|iPad|Android)""".toRegex().matches(window.navigator.userAgent)
