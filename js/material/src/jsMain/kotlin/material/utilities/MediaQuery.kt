package material.utilities

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.w3c.dom.MediaQueryListEvent
import org.w3c.dom.events.Event

// ref. https://material.io/design/layout/responsive-layout-grid.html#breakpoints
private const val DP_PHONE_MIN = 0
private const val DP_TABLET_SMALL_MIN = 600
private const val DP_TABLET_LARGE_MIN = 905
private const val DP_LAPTOP_MIN = 1240
private const val DP_DESKTOP_MIN = 1440

const val MEDIA_QUERY_PHONE = "(min-width: ${DP_PHONE_MIN}px)"
const val MEDIA_QUERY_TABLET_SMALL = "(min-width: ${DP_TABLET_SMALL_MIN}px)"
const val MEDIA_QUERY_TABLET_LARGE = "(min-width: ${DP_TABLET_LARGE_MIN}px)"
const val MEDIA_QUERY_LAPTOP = "(min-width: ${DP_LAPTOP_MIN}px)"
const val MEDIA_QUERY_DESKTOP = "(min-width: ${DP_DESKTOP_MIN}px)"

@Composable
fun rememberMediaQuery(query: String): MutableState<Boolean> {
    val matches = remember(query) { mutableStateOf(false) }
    val listener = remember(query) {
        { e: Event -> matches.value = (e as? MediaQueryListEvent)?.matches == true }
    }

    DisposableEffect(query) {
        val media = window.matchMedia(query).apply { addEventListener("change", listener) }
        matches.value = media.matches

        onDispose { media.removeEventListener("change", listener) }
    }

    return matches
}
