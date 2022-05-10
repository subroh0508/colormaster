import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        Style(MaterialTheme())

        Div { Text("Hello, World!") }
        Div {
            Chip("Chip One") { console.log("click!") }
            OutlinedChip("Chip Two") { console.log("click!") }
        }
    }
}
