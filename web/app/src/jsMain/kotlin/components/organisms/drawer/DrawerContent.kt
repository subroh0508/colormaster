package components.organisms.drawer

import androidx.compose.runtime.Composable
import components.atoms.drawer.DrawerListItem
import components.atoms.list.ListGroupSubHeader
import components.organisms.drawer.menus.SignInMenu
import material.components.Divider
import material.components.DrawerContent as MaterialDrawerContent
import material.externals.MDCDrawer
import material.externals.close
import net.subroh0508.colormaster.presentation.common.external.invoke
import net.subroh0508.colormaster.presentation.common.ui.LocalI18n
import routes.CurrentLocalRouter
import routes.Router

@Composable
fun DrawerContent(
    drawer: MDCDrawer?,
    isMobile: Boolean,
    isSignedOut: Boolean,
) = MaterialDrawerContent {
    val router = CurrentLocalRouter() ?: return@MaterialDrawerContent

    SearchMenu(
        drawer,
        router,
    )
    MyPage(
        drawer,
        router,
        isSignedOut,
    )
    AboutMenu(
        drawer,
        router,
    )
    SignInMenu(
        drawer,
        isMobile,
        isSignedOut,
    )
    LinkMenu(
        drawer,
    )
}

@Composable
private fun MyPage(
    drawer: MDCDrawer?,
    router: Router,
    isSignedOut: Boolean,
) {
    val t = LocalI18n() ?: return
    if (isSignedOut) {
        return
    }

    ListGroupSubHeader(t("appMenu.myPage.label"))
    DrawerListItem(t("appMenu.myPage.myIdols")) {
        attr("aria-current", "page")
        attr("tabindex", "0")
        onClick {
            router.toMyIdols()
            drawer?.close()
        }
    }
    Divider()
}

@Composable
private fun SearchMenu(
    drawer: MDCDrawer?,
    router: Router,
) {
    val t = LocalI18n() ?: return

    ListGroupSubHeader(t("appMenu.search.label"))
    DrawerListItem(t("appMenu.search.attributes")) {
        attr("aria-current", "page")
        attr("tabindex", "0")
        onClick {
            router.toSearch()
            drawer?.close()
        }
    }
    Divider()
}

@Composable
private fun AboutMenu(
    drawer: MDCDrawer?,
    router: Router,
) {
    val t = LocalI18n() ?: return

    ListGroupSubHeader(t("appMenu.about.label"))
    DrawerListItem(t("appMenu.about.howToUse")) {
        onClick {
            router.toHowToUse()
            drawer?.close()
        }
    }
    DrawerListItem(t("appMenu.about.development")) {
        onClick {
            router.toDevelopment()
            drawer?.close()
        }
    }
    DrawerListItem(t("appMenu.about.terms")) {
        onClick {
            router.toTerms()
            drawer?.close()
        }
    }
    Divider()
}

@Composable
private fun LinkMenu(
    drawer: MDCDrawer?,
) {
    val t = LocalI18n() ?: return

    DrawerListItem(
        t("appMenu.links.github"),
        "https://github.com/subroh0508/colormaster",
    ) { onClick { drawer?.close() } }
    DrawerListItem(
        t("appMenu.links.twitter"),
        "https://twitter.com/subroh_0508",
    ) { onClick { drawer?.close() } }
}
