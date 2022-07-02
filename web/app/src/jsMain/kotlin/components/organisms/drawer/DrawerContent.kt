package components.organisms.drawer

import androidx.compose.runtime.Composable
import components.atoms.button.SignInWithGoogle
import components.atoms.drawer.DrawerListItem
import components.atoms.list.ListGroupSubHeader
import components.atoms.tooltip.Tooltip
import material.components.Divider
import material.components.DrawerContent as MaterialDrawerContent
import material.externals.MDCDrawer
import org.jetbrains.compose.web.css.*

@Composable
fun DrawerContent(drawer: MDCDrawer?) = MaterialDrawerContent {
    SearchMenu()
    Divider()
    AboutMenu()
    Divider()
    SignInMenu()
    Divider()
    LinkMenu()
}

@Composable
private fun SearchMenu() {
    ListGroupSubHeader("検索")
    DrawerListItem("名前・属性検索") {
        attr("aria-current", "page")
        attr("tabindex", "0")
    }
}

@Composable
private fun AboutMenu() {
    ListGroupSubHeader("このアプリについて")
    DrawerListItem("使い方")
    DrawerListItem("仕組み")
    DrawerListItem("利用規約")
}

@Composable
private fun SignInMenu() {
    ListGroupSubHeader(
        "sign-in",
        "連携アカウント",
        helpContent = {
            Tooltip(
                it,
                "外部アカウントと連携することで、担当・推しアイドルの登録および閲覧ができるようになります",
            )
        },
    )
    SignInWithGoogle {
        style {
            margin(8.px, 8.px, 8.px, 16.px)
        }
    }
}

@Composable
private fun LinkMenu() {
    DrawerListItem("GitHub", "https://github.com/subroh0508/colormaster")
    DrawerListItem("開発者Twitter", "https://twitter.com/subroh_0508")
}
