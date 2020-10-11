package net.subroh0508.colormaster.androidapp.components.templates

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme

val HEADER_HEIGHT = 56.dp

@Composable
@ExperimentalMaterialApi
fun ModalDrawerBackdrop(
    appBar: @Composable (DrawerState) -> Unit,
    drawerContent: @Composable ColumnScope.() -> Unit,
    backLayerContent: @Composable (BackdropScaffoldState) -> Unit,
    frontLayerContent: @Composable (BackdropScaffoldState) -> Unit,
) {
    val modalDrawerState = rememberDrawerState(DrawerValue.Closed)
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)

    ModalDrawerFrame {
        ModalDrawerLayout(
            drawerState = modalDrawerState,
            drawerContent = drawerContent,
            bodyContent = {
                BackdropScaffold(
                    headerHeight = HEADER_HEIGHT,
                    scaffoldState = backdropScaffoldState,
                    appBar = { appBar(modalDrawerState) },
                    backLayerContent = { backLayerContent(backdropScaffoldState) },
                    frontLayerContent = { frontLayerContent(backdropScaffoldState) },
                    backLayerBackgroundColor = MaterialTheme.colors.background,
                )
            },
        )
    }
}

@Composable
private fun ModalDrawerFrame(children: @Composable () -> Unit) {
    ColorMasterTheme {
        Surface(color = MaterialTheme.colors.background, content = children)
    }
}
