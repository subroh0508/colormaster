package net.subroh0508.colormaster.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import org.koin.dsl.koinApplication

val koinApp = koinApplication { }

val LocalKoinApp = compositionLocalOf { koinApp }

@Composable
fun CurrentLocalKoinApp() = LocalKoinApp.current
