package components.templates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import components.atoms.menu.MenuButton
import components.atoms.tooltip.Tooltip
import components.atoms.topappbar.TopAppActionIcon
import components.molecules.TopAppBar
import components.organisms.drawer.DrawerContent
import components.organisms.drawer.DrawerHeader
import material.components.MenuItem
import material.components.TopAppBarVariant
import material.externals.MDCDrawer
import material.externals.open
import net.subroh0508.colormaster.components.core.external.I18nextText
import net.subroh0508.colormaster.components.core.external.invoke
import net.subroh0508.colormaster.components.core.ui.AppPreference
import net.subroh0508.colormaster.components.core.ui.Languages
import net.subroh0508.colormaster.components.core.ui.LocalApp
import net.subroh0508.colormaster.components.core.ui.ThemeType
import net.subroh0508.colormaster.model.authentication.CurrentUser
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.vh
import routes.CurrentLocalRouter
import usecase.isSignedIn
import usecase.isSignedOut
import usecase.rememberSubscribeCurrentUserUseCase
import utilities.*
import material.components.ModalDrawer as MaterialModalDrawer

@Composable
fun MainFrame(preference: AppPreference) {
    val currentUser by rememberSubscribeCurrentUserUseCase()

    MaterialModalDrawer(
        headerContent = { DrawerHeader() },
        drawerContent = { DrawerContent(it, isMobile, currentUser.isSignedOut) },
        mainContentAttrs = { style { height(100.vh) } },
        mainContent = { DrawerMain(it, preference, currentUser) },
    )
}

@Composable
private fun DrawerMain(
    drawer: MDCDrawer?,
    preference: AppPreference,
    currentUser: CurrentUser?,
    variant: String = TopAppBarVariant.Fixed,
) {
    TopAppBar(variant, drawer, preference)
    Routing(variant, currentUser.isSignedIn)
}

@Composable
private fun TopAppBar(
    variant: String,
    drawer: MDCDrawer?,
    preference: AppPreference,
) = TopAppBar(
    variant,
    onClickNavigation = { drawer?.open() },
    actionContent = { DrawerActionContent(preference) },
)

@Composable
private fun DrawerActionContent(preference: AppPreference) {
    val (lang, theme, t) = LocalApp.current
    val router = CurrentLocalRouter() ?: return
    t ?: return

    MenuButton(
        "translate",
        { classes("mdc-top-app-bar__action-item") },
        icon = "translate",
        tooltip = { Tooltip(it, t("appBar.changeLanguage")) },
    ) {
        MenuItem("日本語", activated = lang == Languages.JAPANESE) {
            router.changeLanguage(Languages.JAPANESE)
        }
        MenuItem("English", activated = lang == Languages.ENGLISH) {
            router.changeLanguage(Languages.ENGLISH)
        }
    }

    TopAppActionIcon(
        theme.icon,
        { onClick { preference.setThemeType(theme.next) } },
        tooltip = { Tooltip(it, t(theme)) },
    )
}

private val ThemeType.icon get() = when (this) {
    ThemeType.DAY -> "brightness_4"
    ThemeType.NIGHT -> "brightness_7"
}

private val ThemeType.next get() = ThemeType.values()[(ordinal + 1) % 2]

private operator fun I18nextText.invoke(theme: ThemeType) = invoke(
    when (theme) {
        ThemeType.DAY -> "appBar.darkTheme"
        ThemeType.NIGHT -> "appBar.lightTheme"
    }
)
