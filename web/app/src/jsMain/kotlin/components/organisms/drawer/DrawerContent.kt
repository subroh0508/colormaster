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
    val i18n = LocalI18n() ?: return

    ListGroupSubHeader(i18n.t("appMenu.search.label"))
    DrawerListItem(i18n.t("appMenu.search.attributes")) {
        attr("aria-current", "page")
        attr("tabindex", "0")
    }
}

@Composable
private fun AboutMenu() {
    val i18n = LocalI18n() ?: return

    ListGroupSubHeader(i18n.t("appMenu.about.label"))
    DrawerListItem(i18n.t("appMenu.about.howToUse"))
    DrawerListItem(i18n.t("appMenu.about.development"))
    DrawerListItem(i18n.t("appMenu.about.terms"))
}

@Composable
private fun SignInMenu() {
    val i18n = LocalI18n() ?: return

    ListGroupSubHeader(
        "sign-in",
        i18n.t("appMenu.account.label"),
        helpContent = {
            Tooltip(
                it,
                i18n.t("appMenu.account.description"),
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
    val i18n = LocalI18n() ?: return

    DrawerListItem(i18n.t("appMenu.links.github"), "https://github.com/subroh0508/colormaster")
    DrawerListItem(i18n.t("appMenu.links.twitter"), "https://twitter.com/subroh_0508")
}
