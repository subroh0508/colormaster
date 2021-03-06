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
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.viewmodel.FavoritesViewModel

@ExperimentalFoundationApi
@Composable
fun Favorites(
    viewModel: FavoritesViewModel,
    drawerState: DrawerState,
    drawerScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val pageScope = rememberCoroutineScope()

    SideEffect(viewModel::search)

    Column {
        HomeTopBar(drawerState, drawerScope, stringResource(R.string.app_menu_favorites))
        Content(viewModel, pageScope, snackbarHostState, launchPreviewScreen)
    }
}

@ExperimentalFoundationApi
@Composable
private fun Content(
    viewModel: FavoritesViewModel,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsState(initial = SearchUiModel.Favorites.INITIALIZED)

    val (messageFavorite, messageUnfavorite) =
        stringResource(R.string.search_success_favorite) to stringResource(R.string.search_success_unfavorite)

    fun showSnackbar(favorite: Boolean) {
        val message = if (favorite) messageFavorite else messageUnfavorite

        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    ColorLists(
        uiModel.items,
        onSelect = viewModel::select,
        onClickFavorite = { (id), favorite ->
            viewModel.favorite(id, favorite)
            viewModel.search()
            showSnackbar(favorite)
        },
        onClick = { launchPreviewScreen(ScreenType.Penlight, listOf(it.id)) },
        onPreviewClick = {
            launchPreviewScreen(
                ScreenType.Preview,
                uiModel.selectedItems.map(IdolColor::id)
            )
        },
        onPenlightClick = {
            launchPreviewScreen(
                ScreenType.Penlight,
                uiModel.selectedItems.map(IdolColor::id)
            )
        },
        onAllClick = viewModel::selectAll,
        modifier = Modifier.fillMaxSize(),
    )
}
