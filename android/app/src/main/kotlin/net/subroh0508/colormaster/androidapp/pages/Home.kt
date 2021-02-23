package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.components.atoms.*
import net.subroh0508.colormaster.androidapp.components.molecules.DrawerMenuList
import net.subroh0508.colormaster.androidapp.components.organisms.ColorLists
import net.subroh0508.colormaster.androidapp.components.organisms.HomeTopBar
import net.subroh0508.colormaster.androidapp.components.organisms.SearchBox
import net.subroh0508.colormaster.androidapp.components.templates.HEADER_HEIGHT
import net.subroh0508.colormaster.androidapp.components.templates.ModalDrawerBackdrop
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchState
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalLayout
@Composable
fun Home(
    viewModel: SearchByNameViewModel,
    lifecycleScope: LifecycleCoroutineScope,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    ModalDrawerBackdrop(
        appBar = { drawerState ->
            HomeTopBar(
                titles = stringArrayResource(R.array.main_tabs),
                drawerState = drawerState,
            )
        },
        drawerContent = { HomeDrawerContent() },
        backLayerContent = { _, _ ->
            BackLayerContent(
                viewModel,
                viewModel::setSearchParams,
            )
        },
        frontLayerContent = { backdropScaffoldState, snackbarHostState ->
            FrontLayerContent(
                viewModel,
                lifecycleScope,
                backdropScaffoldState,
                snackbarHostState,
                launchPreviewScreen,
            )
        },
        bottomBarHeight = 52.dp,
    )
}

@Composable
private fun HomeDrawerContent() {
    Column(Modifier.fillMaxSize()) {
        DrawerHeader(
            title = stringResource(R.string.app_name),
            subtext = "v2020.09.20-beta01",
        )
        DrawerMenuList(
            label = stringResource(R.string.app_menu_search_label),
            items = arrayOf(
                Icons.Default.Search to stringResource(R.string.app_menu_search_attributes),
            ),
            onClick = {},
        )
        DrawerMenuList(
            label = stringResource(R.string.app_menu_about_label),
            items = arrayOf(
                Icons.Default.Search to stringResource(R.string.app_menu_about_how_to_use),
                Icons.Default.Search to stringResource(R.string.app_menu_about_development),
                Icons.Default.Search to stringResource(R.string.app_menu_about_terms),
            ),
            onClick = {},
        )
    }
}

@Composable
@ExperimentalMaterialApi
@ExperimentalLayout
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
    lifecycleScope: LifecycleCoroutineScope,
    backdropScaffoldState: BackdropScaffoldState,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val uiModel by viewModel.uiModel.collectAsState(initial = SearchUiModel.ByName.INITIALIZED)

    fun showSnackbar(message: String) {
        lifecycleScope.launch { snackbarHostState.showSnackbar(message) }
    }

    Column {
        SearchStateLabel(
            uiModel,
            if (backdropScaffoldState.isConcealed)
                Icons.Default.KeyboardArrowDown
            else
                Icons.Default.KeyboardArrowUp,
            onClick = {
                if (backdropScaffoldState.isConcealed)
                    backdropScaffoldState.reveal()
                else
                    backdropScaffoldState.conceal()
            },
            modifier = Modifier
                .fillMaxWidth()
                .preferredHeight(HEADER_HEIGHT)
                .padding(8.dp),
        )
        ColorLists(
            uiModel.items,
            onSelect = viewModel::select,
            onClickFavorite = { (id), favorite ->
                viewModel.favorite(id, favorite)
                showSnackbar(
                    if (favorite)
                        "お気に入りに登録しました"
                    else
                        "お気に入りへの登録を解除しました"
                )
            },
            onClick = { launchPreviewScreen(ScreenType.Penlight, listOf(it.id)) },
            onPreviewClick = { launchPreviewScreen(ScreenType.Preview, uiModel.selectedItems.map(IdolColor::id)) },
            onPenlightClick = { launchPreviewScreen(ScreenType.Penlight, uiModel.selectedItems.map(IdolColor::id)) },
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

@Preview
@Composable
@ExperimentalMaterialApi
@ExperimentalLayout
fun PreviewHome() {
    //Home(uiModel)
}
