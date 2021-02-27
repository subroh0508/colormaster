package net.subroh0508.colormaster.androidapp.components.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.subroh0508.colormaster.androidapp.themes.ColorMasterTheme

@Composable
@ExperimentalMaterialApi
fun ModalDrawerScaffold(
    drawerContent: @Composable ColumnScope.(DrawerState) -> Unit,
    bodyContent: @Composable (DrawerState, SnackbarHostState) -> Unit,
    bottomBarHeight: Dp = 0.dp
) {
    val modalDrawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember(::SnackbarHostState)

    ModalDrawerFrame {
        ModalDrawer(
            drawerState = modalDrawerState,
            drawerContent = { drawerContent(modalDrawerState) },
        ) {
            // Workarount
            Box {
                bodyContent(modalDrawerState, snackbarHostState)
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(start = 8.dp, end = 8.dp, bottom = bottomBarHeight + 8.dp),
                    snackbar = {
                        Snackbar { Text(it.message) }
                    },
                )
            }
        }
    }
}

@Composable
private fun ModalDrawerFrame(children: @Composable () -> Unit) {
    ColorMasterTheme {
        Surface(color = MaterialTheme.colors.background, content = children)
    }
}
