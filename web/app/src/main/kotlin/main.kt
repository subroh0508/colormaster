import kotlin.browser.window

fun main() {
    window.onload = { MainApplication().loadItems() }
}
