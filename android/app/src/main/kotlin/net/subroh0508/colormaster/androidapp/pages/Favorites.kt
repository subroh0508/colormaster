package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.material.DrawerState
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleCoroutineScope
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel

@Composable
fun Favorites(
    viewModel: SearchByNameViewModel,
    lifecycleScope: LifecycleCoroutineScope,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {

}
