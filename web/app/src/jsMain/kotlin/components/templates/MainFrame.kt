package components.templates

import androidx.compose.runtime.Composable
import components.molecules.TopAppBar
import components.organisms.drawer.DrawerContent
import components.organisms.drawer.DrawerHeader
import material.externals.MDCDrawer
import material.externals.open
import org.jetbrains.compose.web.dom.Text
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
    onClickTranslate = {},
    onClickBrightness = {},
) { Text("Hello, World!") }
