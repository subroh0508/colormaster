package components.templates

import androidx.compose.runtime.Composable
import components.atoms.menu.MenuButton
import components.atoms.tooltip.Tooltip
import components.atoms.topappbar.TopAppActionIcon
import components.molecules.TopAppBar
import components.organisms.drawer.DrawerContent
import components.organisms.drawer.DrawerHeader
import material.components.MenuItem
import material.externals.MDCDrawer
import material.externals.open
import net.subroh0508.colormaster.model.ui.commons.AppPreference
import org.jetbrains.compose.web.dom.Text
import utilities.LocalI18n
import material.components.ModalDrawer as MaterialModalDrawer

@Composable
fun MainFrame(preference: AppPreference) {
    MaterialModalDrawer(
        headerContent = { DrawerHeader() },
        drawerContent = { DrawerContent(it) },
        mainContent = { DrawerMain(it, preference) },
    )
}

@Composable
private fun DrawerMain(
    drawer: MDCDrawer?,
    preference: AppPreference,
) = TopAppBar(
    onClickNavigation = { drawer?.open() },
    actionContent = { DrawerActionContent(preference) },
) { Text("Hello, World!") }

@Composable
private fun DrawerActionContent(preference: AppPreference) {
    val i18n = LocalI18n() ?: return

    MenuButton(
        "translate",
        { classes("mdc-top-app-bar__action-item") },
        icon = "translate",
        tooltip = { Tooltip(it, i18n.t("appBar.changeLanguage")) },
    ) {
        MenuItem("日本語")
        MenuItem("English")
    }

    TopAppActionIcon(
        "brightness_4",
        { onClick { preference.toggleThemeType() } },
        tooltip = { Tooltip(it, i18n.t("appBar.darkTheme")) },
    )
}

