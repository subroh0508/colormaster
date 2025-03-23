package components.organisms.drawer

import androidx.compose.runtime.Composable
import material.components.Divider
import org.jetbrains.compose.web.dom.Text
import utilities.APP_NAME
import utilities.APP_VERSION
import material.components.DrawerHeader as MaterialDrawerHeader

@Composable
fun DrawerHeader() {
    MaterialDrawerHeader(
        title = { Text(APP_NAME) },
        subtitle = { Text(APP_VERSION) },
    )
    Divider()
}
