package net.subroh0508.colormaster.androidapp.components.templates

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme

@Composable
@ExperimentalMaterialApi
fun ModalDrawerBackdrop(
    appBar: @Composable (DrawerState) -> Unit,
    drawerContent: @Composable ColumnScope.() -> Unit,
    backLayerContent: @Composable () -> Unit,
    frontLayerContent: @Composable () -> Unit,
) {
    val modalDrawerState = rememberDrawerState(DrawerValue.Closed)
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)

    ModalDrawerFrame {
        ModalDrawerLayout(
            drawerState = modalDrawerState,
            drawerContent = drawerContent,
            bodyContent = {
                BackdropScaffold(
                    scaffoldState = backdropScaffoldState,
                    appBar = { appBar(modalDrawerState) },
                    backLayerContent = backLayerContent,
                    frontLayerContent = frontLayerContent,
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
