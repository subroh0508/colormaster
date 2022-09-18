package components.organisms.drawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import components.atoms.button.SignInWithGoogle
import components.atoms.drawer.DrawerListItem
import components.atoms.list.ListGroupSubHeader
import components.atoms.tooltip.Tooltip
import material.components.Divider
import material.components.DrawerContent as MaterialDrawerContent
import material.externals.MDCDrawer
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.common.LoadState
import org.jetbrains.compose.web.css.*
import usecase.rememberSignInUseCase
import usecase.rememberSignOutUseCase
import utilities.LocalI18n
import utilities.invoke

@Composable
fun DrawerContent(
    drawer: MDCDrawer?,
    isMobile: Boolean,
    currentUserLoadState: MutableState<LoadState>,
) = MaterialDrawerContent {
    val (currentUser, setCurrentUserLoadState) = currentUserLoadState

    SearchMenu()
    Divider()
    AboutMenu()
    Divider()
    SignInMenu(
        isMobile,
        currentUser.getValueOrNull(),
        setCurrentUserLoadState,
    )
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
private fun SignInMenu(
    isMobile: Boolean,
    currentUser: CurrentUser?,
    setCurrentUserLoadState: (LoadState) -> Unit,
) {
    val t = LocalI18n() ?: return
    val signIn = rememberSignInUseCase(isMobile, setCurrentUserLoadState)
    val signOut = rememberSignOutUseCase(setCurrentUserLoadState)

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
    if (currentUser?.isAnonymous != false) {
        SignInWithGoogle {
            style {
                margin(8.px, 8.px, 8.px, 16.px)
            }
            onClick { signIn() }
        }

        return
    }

    DrawerListItem(t("appMenu.account.signOut")) {
        onClick { signOut() }
    }
}

@Composable
private fun LinkMenu() {
    val t = LocalI18n() ?: return

    DrawerListItem(t("appMenu.links.github"), "https://github.com/subroh0508/colormaster")
    DrawerListItem(t("appMenu.links.twitter"), "https://twitter.com/subroh_0508")
}
