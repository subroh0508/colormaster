package components.organisms.drawer.menus

import androidx.compose.runtime.Composable
import components.atoms.button.SignInWithGoogle
import components.atoms.drawer.DrawerListItem
import components.atoms.list.ListGroupSubHeader
import components.atoms.tooltip.Tooltip
import material.components.Divider
import material.externals.MDCDrawer
import material.externals.close
import net.subroh0508.colormaster.components.core.external.invoke
import net.subroh0508.colormaster.components.core.ui.LocalI18n
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.px
import usecase.rememberSignInUseCase
import usecase.rememberSignOutUseCase

@Composable
fun SignInMenu(
    drawer: MDCDrawer?,
    isMobile: Boolean,
    isSignedOut: Boolean,
) {
    val t = LocalI18n() ?: return
    val signIn = rememberSignInUseCase(isMobile)
    val signOut = rememberSignOutUseCase()

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

    if (isSignedOut) {
        SignInWithGoogle {
            style {
                margin(8.px, 8.px, 8.px, 16.px)
            }
            onClick {
                signIn()
                drawer?.close()
            }
        }
    } else {
        DrawerListItem(
            t("appMenu.account.signOut"),
        ) {
            onClick {
                signOut()
                drawer?.close()
            }
        }
    }

    Divider()
}
