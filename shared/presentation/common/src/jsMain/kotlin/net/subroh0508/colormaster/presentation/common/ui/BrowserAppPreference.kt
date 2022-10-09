package net.subroh0508.colormaster.presentation.common.ui

import androidx.compose.runtime.*
import net.subroh0508.colormaster.presentation.common.external.I18nextText
import org.w3c.dom.Storage
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.reflect.KProperty

actual class AppPreference(
    localStorage: Storage,
    private val state: MutableState<State>,
) {
    actual val lang get() = state.value.lang
    actual val theme get() = themeType ?: ThemeType.DAY

    actual fun setThemeType(type: ThemeType) {
        themeType = type
        state.value = state.value.copy(theme = type)
    }

    private var themeType by localStorage

    actual data class State(
        actual val lang: Languages = Languages.JAPANESE,
        actual val theme: ThemeType = ThemeType.DAY,
        val i18n: I18nextText? = null,
    ) {
        constructor(localStorage: Storage) : this(
            theme = localStorage["themeType"]?.let {
                ThemeType.valueOf(it.uppercase())
            } ?: ThemeType.DAY,
        )

        val dark = theme == ThemeType.NIGHT
    }
}

actual val LocalApp: ProvidableCompositionLocal<AppPreference.State> = compositionLocalOf { AppPreference.State() }

val Languages.basename get() = when (this) {
    Languages.JAPANESE -> ""
    Languages.ENGLISH -> "/en"
}

@Composable
fun LocalI18n() = LocalApp.current.i18n

private operator fun Storage.getValue(
    thisRef: AppPreference,
    property: KProperty<*>,
) = get(property.name)?.let { ThemeType.valueOf(it.uppercase()) }

private operator fun Storage.setValue(
    thisRef: AppPreference,
    property: KProperty<*>,
    value: ThemeType?,
) { if (value == null) removeItem(property.name) else set(property.name, value.name) }
