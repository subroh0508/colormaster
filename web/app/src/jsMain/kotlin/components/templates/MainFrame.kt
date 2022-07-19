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
import org.jetbrains.compose.web.dom.Text
import utilities.LocalBrowserApp
import material.components.ModalDrawer as MaterialModalDrawer

@Composable
fun MainFrame() {
    MaterialModalDrawer(
        headerContent = { DrawerHeader() },
        drawerContent = { DrawerContent(it) },
        mainContent = { DrawerMain(it) },
    )
}

@Composable
private fun DrawerMain(drawer: MDCDrawer?) = TopAppBar(
    onClickNavigation = { drawer?.open() },
    actionContent = { DrawerActionContent() },
) { Text("Hello, World!") }

@Composable
private fun DrawerActionContent() {
    val (i18n, _) = LocalBrowserApp.current ?: return

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
        tooltip = { Tooltip(it, i18n.t("appBar.darkTheme")) },
    )
}

