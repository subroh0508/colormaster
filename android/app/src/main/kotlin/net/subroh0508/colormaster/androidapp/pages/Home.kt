package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.atoms.*
import net.subroh0508.colormaster.androidapp.components.molecules.DrawerMenuList
import net.subroh0508.colormaster.androidapp.components.organisms.ColorLists
import net.subroh0508.colormaster.androidapp.components.organisms.HomeTopBar
import net.subroh0508.colormaster.androidapp.components.organisms.SearchBox
import net.subroh0508.colormaster.androidapp.components.templates.ModalDrawerBackdrop
import net.subroh0508.colormaster.androidapp.viewmodel.IdolSearchViewModel
import net.subroh0508.colormaster.model.HexColor
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.UiModel
import net.subroh0508.colormaster.model.ui.idol.Filters
import net.subroh0508.colormaster.model.ui.idol.SearchState

@Composable
@ExperimentalMaterialApi
@ExperimentalLayout
fun Home(viewModel: IdolSearchViewModel) {
    val uiModel: UiModel.Search by viewModel.uiModel.collectAsState(initial = UiModel.Search.INITIALIZED)

    ModalDrawerBackdrop(
        appBar = { drawerState ->
            HomeTopBar(
                titles = stringArrayResource(R.array.main_tabs),
                drawerState = drawerState,
            )
        },
        drawerContent = { HomeDrawerContent() },
        backLayerContent = { BackLayerContent(uiModel.filters) { viewModel.searchParams = it } },
        frontLayerContent = { FrontLayerContent(uiModel) },
    )
}

@Composable
private fun HomeDrawerContent() {
    Column(Modifier.fillMaxSize()) {
        DrawerHeader(
            title = stringResource(R.string.app_name),
            subtext = "v2020.09.20.01-beta",
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
private fun BackLayerContent(filters: Filters, onFiltersChange: (Filters) -> Unit) {
    Surface(color = MaterialTheme.colors.background) {
        SearchBox(
            filters,
            onFiltersChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun FrontLayerContent(uiModel: UiModel.Search) {
    Column {
        val modifier = Modifier.padding(8.dp)

        SearchStateLabel(uiModel, modifier = modifier.fillMaxWidth())
        ColorLists(uiModel.items, modifier = modifier)
    }
}

@Composable
private fun SearchStateLabel(
    uiModel: UiModel.Search,
    modifier: Modifier,
) = when (uiModel.searchState) {
    SearchState.RANDOM -> InfoAlert(message = stringResource(R.string.search_state_label_random), modifier = modifier)
    SearchState.WAITING -> WarningAlert(message = stringResource(R.string.search_state_label_waiting), modifier = modifier)
    SearchState.SEARCHED -> SuccessAlert(message = stringResource(R.string.search_state_label_searched, uiModel.items.size), modifier = modifier)
    SearchState.ERROR -> ErrorAlert(message = stringResource(R.string.search_state_label_error, uiModel.error?.message ?: ""), modifier = modifier)
}

@Preview
@Composable
@ExperimentalMaterialApi
@ExperimentalLayout
fun PreviewHome() {
    val uiModel = UiModel.Search(
        listOf(
            UiModel.Search.IdolColorItem(IdolColor("Mitsumine_Yuika", "三峰結華", HexColor("3B91C4"))),
            UiModel.Search.IdolColorItem(IdolColor("Hachimiya_Meguru", "八宮めぐる", HexColor("FFE012"))),
            UiModel.Search.IdolColorItem(IdolColor("Higuchi_Madoka", "樋口円香",  HexColor("BE1E3E"))),
            UiModel.Search.IdolColorItem(IdolColor("Arisugawa_Natsuha", "有栖川夏葉", HexColor("90E677"))),
        ),
        Filters.Empty,
        error = null,
    )

    //Home(uiModel)
}
