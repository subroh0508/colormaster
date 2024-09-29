package net.subroh0508.colormaster.features.home

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.components.core.CurrentLocalKoinApp
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.repository.AuthenticationRepository
import org.koin.core.KoinApplication

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

val CurrentUser?.isSignedOut get() = this?.isAnonymous != false
val CurrentUser?.isSignedIn get() = !isSignedOut
