package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.components.atoms.ErrorAlert
import net.subroh0508.colormaster.androidapp.components.atoms.InfoAlert
import net.subroh0508.colormaster.androidapp.components.atoms.SuccessAlert
import net.subroh0508.colormaster.androidapp.components.atoms.WarningAlert
import net.subroh0508.colormaster.androidapp.components.organisms.ColorLists
import net.subroh0508.colormaster.androidapp.components.organisms.HomeTopBar
import net.subroh0508.colormaster.androidapp.components.organisms.SearchBox
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchState
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel

private val HEADER_HEIGHT = 56.dp

@ExperimentalLayoutApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Search(
    viewModel: SearchByNameViewModel,
    drawerState: DrawerState,
    drawerScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    val pageScope = rememberCoroutineScope()

    SideEffect {
        viewModel.search()
        viewModel.loadFavorites()
    }

    BackdropScaffold(
        headerHeight = HEADER_HEIGHT,
        scaffoldState = backdropScaffoldState,
        appBar = { HomeTopBar(drawerState, drawerScope, stringArrayResource(R.array.main_tabs)) },
        backLayerContent = {
            BackLayerContent(
                viewModel,
                viewModel::setSearchParams,
            )
        },
        frontLayerContent = {
            FrontLayerContent(
                viewModel,
                pageScope,
                backdropScaffoldState,
                snackbarHostState,
                launchPreviewScreen,
            )
        },
        backLayerBackgroundColor = MaterialTheme.colors.background,
    )
}

@Composable
@ExperimentalMaterialApi
@ExperimentalLayoutApi
private fun BackLayerContent(
    viewModel: SearchByNameViewModel,
    onParamsChange: (SearchParams) -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsState(initial = SearchUiModel.ByName.INITIALIZED)

    SearchBox(
        uiModel.params,
        onParamsChange = onParamsChange,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colors.background),
    )
}

@ExperimentalFoundationApi
@Composable
@ExperimentalMaterialApi
private fun FrontLayerContent(
    viewModel: SearchByNameViewModel,
    coroutineScope: CoroutineScope,
    backdropScaffoldState: BackdropScaffoldState,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsState(initial = SearchUiModel.ByName.INITIALIZED)

    fun showSnackbar(message: String) {
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    Column {
        SearchStateLabel(
            uiModel,
            if (backdropScaffoldState.isConcealed)
                Icons.Default.KeyboardArrowDown
            else
                Icons.Default.KeyboardArrowUp,
            onClick = {
                coroutineScope.launch {
                    if (backdropScaffoldState.isConcealed)
                        backdropScaffoldState.reveal()
                    else
                        backdropScaffoldState.conceal()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(HEADER_HEIGHT)
                .padding(8.dp),
        )

        val (messageFavorite, messageUnfavorite) =
            stringResource(R.string.search_success_favorite) to stringResource(R.string.search_success_unfavorite)

        ColorLists(
            uiModel.items,
            onSelect = viewModel::select,
            onClickFavorite = { (id), favorite ->
                viewModel.favorite(id, favorite)
                showSnackbar(
                    if (favorite)
                        messageFavorite
                    else
                        messageUnfavorite
                )
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
}

@Composable
private fun SearchStateLabel(
    uiModel: SearchUiModel,
    endAsset: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier,
) = when (uiModel.searchState) {
    SearchState.RANDOM -> InfoAlert(stringResource(R.string.search_state_label_random), modifier, endAsset, onClick)
    SearchState.WAITING -> WarningAlert(stringResource(R.string.search_state_label_waiting), modifier, endAsset, onClick)
    SearchState.SEARCHED -> SuccessAlert(stringResource(R.string.search_state_label_searched, uiModel.items.size), modifier, endAsset, onClick)
    SearchState.ERROR -> ErrorAlert(stringResource(R.string.search_state_label_error, uiModel.error?.message ?: ""), modifier, endAsset, onClick)
}
