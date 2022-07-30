package components.organisms.drawer

import androidx.compose.runtime.Composable
import material.components.Divider
import org.jetbrains.compose.web.dom.Text
import utilities.LocalI18n
import utilities.invoke
import material.components.DrawerHeader as MaterialDrawerHeader

@Composable
fun DrawerHeader() {
    val t = LocalI18n() ?: return

    MaterialDrawerHeader(
        title = { Text(t("title")) },
        subtitle = { Text(t("version")) },
    )
    Divider()
}
