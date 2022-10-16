package net.subroh0508.colormaster.features.home

import androidx.compose.runtime.*
import net.subroh0508.colormaster.components.core.CurrentLocalKoinApp
import org.koin.core.KoinApplication

expect class SignInUseCase

@Composable
expect fun rememberSignInUseCase(
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): SignInUseCase

