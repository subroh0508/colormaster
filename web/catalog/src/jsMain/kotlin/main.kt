import components.*
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        Style(MaterialTheme())

        Div { Text("Hello, World!") }
        Div({ style { padding(16.px) } }) {
            Chip("Chip One") { console.log("click!") }
            OutlinedChip("Chip Two") { console.log("click!") }
        }
        Div({ style { padding(16.px) } }) {
            OutlinedTextField("名前を入力")
        }
        Div({ style { padding(16.px) } }) {
            Checkbox("チェックボックス 1")
            Checkbox("チェックボックス 2", value = true)
        }
        Div({ style { padding(16.px) } }) {
            OutlinedButton("ボタン 1") { console.log("click!") }
            OutlinedButton("ボタン 2", "bookmark") { console.log("click!") }
        }

        Div({ style { padding(16.px) } }) {
            ListGroup {
                ListGroupSubHeader { Text("ヘッダー 1") }
                List {
                    ListItem { Text("リスト 1") }
                    ListItem { Text("リスト 2") }
                    ListItem { Text("リスト 3") }
                }

                ListGroupSubHeader { Text("ヘッダー 2") }
                List {
                    ListItem { Text("リスト 3") }
                    ListItem { Text("リスト 4") }
                    ListItem { Text("リスト 5") }
                }
            }
        }
    }
}
