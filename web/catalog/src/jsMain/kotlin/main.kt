import androidx.compose.runtime.Composable
import material.components.*
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposableInBody

fun main() {
    renderComposableInBody {
        Style(MaterialTheme())

        ModalDrawer(
            headerContent = {
                DrawerHeader(
                    title = "タイトル",
                    subtitle = "リリース日: 2022.01.01",
                )
                Divider()
            },
            drawerContent = {
                DrawerContent {
                    ListGroupSubHeader("ヘッダー 1")
                    DrawerListItem("リスト 1") {
                        attr("aria-current", "page")
                        attr("tabindex", "-1")
                    }
                    DrawerListItem("リスト 2", activated = true)
                    DrawerListItem("リスト 3")

                    Divider()
                    ListGroupSubHeader("ヘッダー 2")
                    DrawerListItem("リスト 3")
                    DrawerListItem("リスト 4")
                    DrawerListItem("リスト 5")
                }
            },
            mainContent = { drawer ->
                TopAppBar(
                    TopAppBarVariant.Fixed,
                    navigationContent = {
                        IconButton("menu") {
                            onClick { drawer?.open = true }
                        }
                        TopAppTitle("Sample App")
                    },
                ) { MainContent() }
            },
        )
    }
}

@Composable
private fun MainContent() {
    Div({ style { padding(16.px) } }) {
        Chip("Chip One") { console.log("click!") }
        Chip("Chip Two", selected = true) { console.log("click!") }
    }
    Div({ style { padding(16.px) } }) {
        OutlinedTextField("名前を入力", onChange = {})
        OutlinedTextArea("名前を入力(複数行)", onChange = {})
    }
    Div({ style { padding(16.px) } }) {
        Menu(
            anchor = { menu ->
                TextButton(
                    "メニュー",
                    {
                        onClick { menu?.open = true }
                        attr("aria-describedby", "menu-id")
                    },
                    trailingIcon = "expand_more",
                )
            },
        ) {
            MenuItem("日本語")
            MenuItem("English")
        }
        Tooltip("menu-id", "言語切り替え")
    }
    Div({ style { padding(16.px) } }) {
        Checkbox(label = "チェックボックス 1")
        Checkbox(label = "チェックボックス 2", value = true)
    }
    Div({ style { padding(16.px) } }) {
        ButtonGroup {
            OutlinedButton("ボタン 1", { onClick { console.log("click!") } })
            OutlinedButton("ボタン 2", { onClick { console.log("click!") } }, "bookmark")
            OutlinedButton("ボタン 3", { onClick { console.log("click!") } }, "bookmark")
        }
    }
    Div({ style { padding(16.px) } }) {
        TabBar(0, TabContent("タブ 1"), TabContent("タブ 2"), TabContent("タブ 3"))
        TabBar(2, TabContent("タブ 4", "favorites"), TabContent("タブ 5", "access_time"), TabContent("タブ 6", "near_me"))
    }
    Div({ style { padding(16.px) } }) {
        Card {
            CardHeader {
                TypographyHeadline5 { Text("タイトル") }
            }
            CardContent {
                Text("吾輩は猫である。名前はまだない。")
            }
        }
    }
    Div({ style { padding(16.px) } }) {
        OutlinedCard {
            CardHeader {
                TypographyHeadline5 { Text("タイトル") }
            }
            CardContent {
                Text("吾輩は猫である。名前はまだない。")
            }
        }
    }
}
