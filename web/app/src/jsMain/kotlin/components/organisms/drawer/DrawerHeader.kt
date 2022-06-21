package components.organisms.drawer

import androidx.compose.runtime.Composable
import material.components.Divider
import org.jetbrains.compose.web.dom.Text
import material.components.DrawerHeader as MaterialDrawerHeader

@Composable
fun DrawerHeader() {
    MaterialDrawerHeader(
        title = { Text("COLOR M@STER") },
        subtitle = { Text("v2022.06.22") },
    )
    Divider()
}
