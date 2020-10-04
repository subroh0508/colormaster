package net.subroh0508.colormaster.androidapp.components.templates

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme

@Composable
fun ModalDrawer(
    drawerContent: @Composable ColumnScope.() -> Unit,
    bodyContent: @Composable (DrawerState) -> Unit,
) {
    val modalDrawerState = rememberDrawerState(DrawerValue.Closed)

    ModalDrawerFrame {
        ModalDrawerLayout(
            drawerState = modalDrawerState,
            drawerContent = drawerContent,
            bodyContent = { bodyContent(modalDrawerState) },
        )
    }
}

@Composable
private fun ModalDrawerFrame(children: @Composable () -> Unit) {
    ColorMasterTheme {
        Surface(color = MaterialTheme.colors.background, content = children)
    }
}
