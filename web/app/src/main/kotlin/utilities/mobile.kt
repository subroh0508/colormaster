package utilities

import kotlinx.browser.window

val isMobile: Boolean get() = window.navigator.userAgent.matches("""(iPhone|iPad|Android)""")
