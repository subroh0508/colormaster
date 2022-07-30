package components.organisms.drawer

import androidx.compose.runtime.Composable
import material.components.Divider
import org.jetbrains.compose.web.dom.Text
import utilities.LocalI18n
import material.components.DrawerHeader as MaterialDrawerHeader

@Composable
fun DrawerHeader() {
    val i18n = LocalI18n() ?: return

    MaterialDrawerHeader(
        title = { Text(i18n.t("title")) },
        subtitle = { Text(i18n.t("version")) },
    )
    Divider()
}
