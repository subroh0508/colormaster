package net.subroh0508.colormaster.common.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

actual class AppPreference {
    actual val lang get() = _lang
    actual val theme get() = _themeType

    fun setLanguage(lang: Languages) { _lang = lang }
    actual fun setThemeType(type: ThemeType) { _themeType = type }

    private var _lang: Languages = Languages.JAPANESE
    private var _themeType: ThemeType = ThemeType.DAY

    actual data class State(
        actual val lang: Languages = Languages.JAPANESE,
        actual val theme: ThemeType = ThemeType.DAY,
    )
}

actual val LocalApp: ProvidableCompositionLocal<AppPreference.State> = compositionLocalOf { AppPreference.State() }
