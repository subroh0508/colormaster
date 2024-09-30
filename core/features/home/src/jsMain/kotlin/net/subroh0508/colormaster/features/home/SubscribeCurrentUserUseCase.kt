package net.subroh0508.colormaster.features.home

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.common.CurrentLocalKoinApp
import net.subroh0508.colormaster.model.auth.CurrentUser
import net.subroh0508.colormaster.data.AuthRepository
import org.koin.core.KoinApplication

@Composable
fun rememberSubscribeCurrentUserUseCase(
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): State<CurrentUser?> {
    val scope = rememberCoroutineScope()
    val repository: AuthRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return produceState<CurrentUser?>(
        initialValue = null,
    ) {
        repository.currentUser().onEach {
            value = it
        }.launchIn(scope)
    }
}

val CurrentUser?.isSignedOut get() = this?.isAnonymous != false
val CurrentUser?.isSignedIn get() = !isSignedOut
