package net.subroh0508.colormaster.features.home

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.common.CurrentLocalKoinApp
import net.subroh0508.colormaster.model.auth.AuthRepository
import org.koin.core.KoinApplication

class SignOutUseCase(
    private val repository: AuthRepository,
    private val scope: CoroutineScope,
) {
    operator fun invoke() {
        scope.launch {
            runCatching { repository.signOut() }
        }
    }
}

@Composable
fun rememberSignOutUseCase(
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): SignOutUseCase {
    val scope = rememberCoroutineScope()
    val repository: AuthRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return remember(koinApp) { SignOutUseCase(repository, scope) }
}
