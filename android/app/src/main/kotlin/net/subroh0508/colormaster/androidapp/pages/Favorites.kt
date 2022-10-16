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
import net.subroh0508.colormaster.features.myidols.rememberAddIdolToFavoriteUseCase
import net.subroh0508.colormaster.features.myidols.rememberFetchFavoriteIdolsUseCase
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
    val loadState by rememberFetchFavoriteIdolsUseCase(isSignedIn = false)

    val items: List<IdolColor> = loadState.getValueOrNull() ?: listOf()

    val (selections, setSelections) = remember(loadState) { mutableStateOf<List<String>>(listOf()) }

    val (messageFavorite, messageUnfavorite) =
        stringResource(R.string.search_success_favorite) to stringResource(R.string.search_success_unfavorite)

    fun showSnackbar(favorite: Boolean) {
        val message = if (favorite) messageFavorite else messageUnfavorite

        coroutineScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    ColorLists(
        items,
        selections,
        onSelect = { item, selected ->
            setSelections(if (selected) selections + listOf(item.id) else selections - listOf(item.id))
        },
        onClickFavorite = { _, _ -> },
        onClick = { launchPreviewScreen(ScreenType.Penlight, listOf(it.id)) },
        onPreviewClick = {
            launchPreviewScreen(
                ScreenType.Preview,
                selections,
            )
        },
        onPenlightClick = {
            launchPreviewScreen(
                ScreenType.Penlight,
                selections,
            )
        },
        onAllClick = { selected ->
            setSelections(if (selected) items.map(IdolColor::id) else listOf())
        },
        modifier = Modifier.fillMaxSize(),
    )
}
