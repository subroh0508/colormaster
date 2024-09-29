package net.subroh0508.colormaster.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal

expect class AppPreference {
    val lang: Languages
    val theme: ThemeType

    fun setThemeType(type: ThemeType)

    class State {
        val lang: Languages
        val theme: ThemeType

        operator fun component1(): Languages
        operator fun component2(): ThemeType
    }
}

expect val LocalApp: ProvidableCompositionLocal<AppPreference.State>

@Composable
fun CurrentLocalTheme() = LocalApp.current.theme

@Composable
fun CurrentLocalLanguage() = LocalApp.current.lang
