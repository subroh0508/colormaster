package components.organisms.drawer

import androidx.compose.runtime.Composable
import components.atoms.list.ListGroupSubHeader
import material.components.Divider
import material.components.DrawerListItem
import material.components.Icon
import material.components.DrawerContent as MaterialDrawerContent
import material.externals.MDCDrawer
import org.jetbrains.compose.web.dom.Text

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
        attr("tabindex", "-1")
    }
}

@Composable
private fun AboutMenu() {
    ListGroupSubHeader("このアプリについて")
    DrawerListItem("使い方") {
        attr("tabindex", "-1")
    }
    DrawerListItem("仕組み")
    DrawerListItem("利用規約")
}

@Composable
private fun SignInMenu() {
    ListGroupSubHeader {
        Text("連携アカウント")
    }
}

@Composable
private fun LinkMenu() {
    DrawerListItem({ attr("tabindex", "-1") }) {
        Icon("launch")
        Text("GitHub")
    }

    DrawerListItem({ attr("tabindex", "-1") }) {
        Icon("launch")
        Text("開発者Twitter")
    }
}
