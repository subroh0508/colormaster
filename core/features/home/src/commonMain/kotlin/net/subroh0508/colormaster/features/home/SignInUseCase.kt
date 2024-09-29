package net.subroh0508.colormaster.features.home

import androidx.compose.runtime.*
import net.subroh0508.colormaster.common.CurrentLocalKoinApp
import org.koin.core.KoinApplication

expect class SignInUseCase

// @see: https://youtrack.jetbrains.com/issue/KT-49563
@Composable
expect fun rememberSignInUseCase(
    koinApp: KoinApplication /* = CurrentLocalKoinApp() */,
): SignInUseCase

@Composable
fun rememberSignInUseCase() = rememberSignInUseCase(CurrentLocalKoinApp())
