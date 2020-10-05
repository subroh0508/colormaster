package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.atoms.DrawerHeader
import net.subroh0508.colormaster.androidapp.components.molecules.DrawerMenuList
import net.subroh0508.colormaster.androidapp.components.organisms.HomeTopBar
import net.subroh0508.colormaster.androidapp.components.organisms.SearchBox
import net.subroh0508.colormaster.androidapp.components.templates.ModalDrawerBackdrop
import net.subroh0508.colormaster.model.ui.idol.Filters

@Composable
@ExperimentalMaterialApi
@ExperimentalLayout
fun Home() {
    ModalDrawerBackdrop(
        appBar = { drawerState ->
            HomeTopBar(
                titles = stringArrayResource(R.array.main_tabs),
                drawerState = drawerState,
            )
        },
        drawerContent = { HomeDrawerContent() },
        backLayerContent = { BackLayerContent() },
        frontLayerContent = {
            Text("FrontLayerContent")
        },
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
private fun BackLayerContent() {
    val (filters, setFilters) = remember { mutableStateOf<Filters>(Filters.Empty)}

    Surface(color = MaterialTheme.colors.background) {
        SearchBox(
            filters,
            onFiltersChange = { setFilters(it) },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Preview
@Composable
@ExperimentalMaterialApi
@ExperimentalLayout
fun PreviewHome() {
    Home()
}
