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
import net.subroh0508.colormaster.common.model.LoadState
import net.subroh0508.colormaster.features.myidols.rememberAddIdolToFavoriteUseCase
import net.subroh0508.colormaster.features.myidols.rememberAddIdolToInChargeUseCase
import net.subroh0508.colormaster.features.search.model.SearchParams
import net.subroh0508.colormaster.features.search.rememberSearchIdolsUseCase
import net.subroh0508.colormaster.model.IdolColor

private val HEADER_HEIGHT = 56.dp

@ExperimentalLayoutApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun Search(
    drawerState: DrawerState,
    drawerScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    val pageScope = rememberCoroutineScope()

    val (params, setParams) = remember { mutableStateOf<SearchParams>(SearchParams.ByName.EMPTY) }

    SideEffect {
        //viewModel.search()
        //viewModel.loadInCharges()
        //viewModel.loadFavorites()
    }

    BackdropScaffold(
        headerHeight = HEADER_HEIGHT,
        scaffoldState = backdropScaffoldState,
        appBar = { HomeTopBar(drawerState, drawerScope, stringArrayResource(R.array.main_tabs)) },
        backLayerContent = {
            BackLayerContent(params, setParams)
        },
        frontLayerContent = {
            FrontLayerContent(
                params,
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
    params: SearchParams,
    onParamsChange: (SearchParams) -> Unit,
) = SearchBox(
    params,
    onParamsChange = onParamsChange,
    modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .background(MaterialTheme.colors.background),
)

@ExperimentalFoundationApi
@Composable
@ExperimentalMaterialApi
private fun FrontLayerContent(
    params: SearchParams,
    coroutineScope: CoroutineScope,
    backdropScaffoldState: BackdropScaffoldState,
    snackbarHostState: SnackbarHostState,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val idolColorLoadState by rememberSearchIdolsUseCase(params)

    val items: List<IdolColor> = idolColorLoadState.getValueOrNull() ?: listOf()

    val (selections, setSelections) = remember(idolColorLoadState) { mutableStateOf<List<String>>(listOf()) }

    val favorites = rememberAddIdolToFavoriteUseCase(isSignedIn = false)
    val inCharges = rememberAddIdolToInChargeUseCase(isSignedIn = false)

    fun showSnackbar(message: String) {
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    Column {
        SearchStateLabel(
            params,
            idolColorLoadState,
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
            items,
            selections,
            onSelect = { item, selected ->
                setSelections(if (selected) selections + listOf(item.id) else selections - listOf(item.id))
            },
            onClickFavorite = { (id), favorite ->
                favorites.add(id, favorite)
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
}

@Composable
private fun SearchStateLabel(
    params: SearchParams,
    loadState: LoadState,
    endAsset: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier,
) = when  {
    loadState is LoadState.Loading -> WarningAlert(stringResource(R.string.search_state_label_waiting), modifier, endAsset, onClick)
    loadState is LoadState.Error -> ErrorAlert(stringResource(R.string.search_state_label_error, loadState.error.message ?: ""), modifier, endAsset, onClick)
    loadState is LoadState.Loaded<*> && !params.isEmpty() -> SuccessAlert(stringResource(R.string.search_state_label_searched, loadState.getValueOrNull<List<IdolColor>>()?.size ?: 0), modifier, endAsset, onClick)
    else -> InfoAlert(stringResource(R.string.search_state_label_random), modifier, endAsset, onClick)
}
