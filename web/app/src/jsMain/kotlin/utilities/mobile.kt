package utilities

import kotlinx.browser.window

val isMobile: Boolean get() = """(iPhone|iPad|Android)""".toRegex().matches(window.navigator.userAgent)
