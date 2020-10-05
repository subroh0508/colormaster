package net.subroh0508.colormaster.androidapp.pages

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.components.atoms.DrawerHeader
import net.subroh0508.colormaster.androidapp.components.molecules.DrawerMenuList
import net.subroh0508.colormaster.androidapp.components.organisms.HomeTopBar
import net.subroh0508.colormaster.androidapp.components.templates.ModalDrawer

@Composable
@ExperimentalMaterialApi
fun Home() {
    ModalDrawer(
        drawerContent = { HomeDrawerContent() },
        bodyContent = { HomeBodyContent(it) },
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
private fun HomeBodyContent(drawerState: DrawerState) {
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)

    BackdropScaffold(
        scaffoldState = backdropScaffoldState,
        appBar = {
            HomeTopBar(
                titles = stringArrayResource(R.array.main_tabs),
                drawerState = drawerState,
            )
        },
        backLayerContent = { Text("BackLayerContent") },
        frontLayerContent = { Text("FrontLayerContent") },
    )
}

@Preview
@Composable
@ExperimentalMaterialApi
fun PreviewHome() {
    Home()
}
