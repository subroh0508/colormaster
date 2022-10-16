package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.DrawerState
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.components.organisms.ColorLists
import net.subroh0508.colormaster.androidapp.components.organisms.HomeTopBar
import net.subroh0508.colormaster.model.IdolColor

@ExperimentalFoundationApi
@Composable
fun Favorites(
    drawerState: DrawerState,
    drawerScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val pageScope = rememberCoroutineScope()

    SideEffect { /* viewModel.search() */ }

    Column {
        HomeTopBar(drawerState, drawerScope, stringResource(R.string.app_menu_favorites))
        Content(pageScope, snackbarHostState, launchPreviewScreen)
    }
}

@ExperimentalFoundationApi
@Composable
private fun Content(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val (messageFavorite, messageUnfavorite) =
        stringResource(R.string.search_success_favorite) to stringResource(R.string.search_success_unfavorite)

    fun showSnackbar(favorite: Boolean) {
        val message = if (favorite) messageFavorite else messageUnfavorite

        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    ColorLists(
        listOf(),
        listOf(),
        onSelect = { item, selected -> },
        onClickFavorite = { (id), favorite ->
            // viewModel.favorite(id, favorite)
            // viewModel.search()
            showSnackbar(favorite)
        },
        onClick = { launchPreviewScreen(ScreenType.Penlight, listOf(it.id)) },
        onPreviewClick = {
            launchPreviewScreen(
                ScreenType.Preview,
                listOf(), /* uiModel.selectedItems.map(IdolColor::id) */
            )
        },
        onPenlightClick = {
            launchPreviewScreen(
                ScreenType.Penlight,
                listOf(), /* uiModel.selectedItems.map(IdolColor::id) */
            )
        },
        onAllClick = { selected -> },
        modifier = Modifier.fillMaxSize(),
    )
}
