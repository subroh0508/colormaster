package components.organisms.drawer

import androidx.compose.runtime.Composable
import material.components.Divider
import org.jetbrains.compose.web.dom.Text
import utilities.LocalBrowserApp
import material.components.DrawerHeader as MaterialDrawerHeader

@Composable
fun DrawerHeader() {
    val (i18n, _) = LocalBrowserApp.current ?: return

    MaterialDrawerHeader(
        title = { Text(i18n.t("title")) },
        subtitle = { Text(i18n.t("version")) },
    )
    Divider()
}
