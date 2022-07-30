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
import utilities.LocalI18n
import utilities.invoke

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
    val t = LocalI18n() ?: return

    ListGroupSubHeader(t("appMenu.search.label"))
    DrawerListItem(t("appMenu.search.attributes")) {
        attr("aria-current", "page")
        attr("tabindex", "0")
    }
}

@Composable
private fun AboutMenu() {
    val t = LocalI18n() ?: return

    ListGroupSubHeader(t("appMenu.about.label"))
    DrawerListItem(t("appMenu.about.howToUse"))
    DrawerListItem(t("appMenu.about.development"))
    DrawerListItem(t("appMenu.about.terms"))
}

@Composable
private fun SignInMenu() {
    val t = LocalI18n() ?: return

    ListGroupSubHeader(
        "sign-in",
        t("appMenu.account.label"),
        helpContent = {
            Tooltip(
                it,
                t("appMenu.account.description"),
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
    val t = LocalI18n() ?: return

    DrawerListItem(t("appMenu.links.github"), "https://github.com/subroh0508/colormaster")
    DrawerListItem(t("appMenu.links.twitter"), "https://twitter.com/subroh_0508")
}
