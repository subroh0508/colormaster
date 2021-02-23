package net.subroh0508.colormaster.androidapp.pages

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleCoroutineScope
import net.subroh0508.colormaster.androidapp.R
import net.subroh0508.colormaster.androidapp.ScreenType
import net.subroh0508.colormaster.androidapp.components.atoms.*
import net.subroh0508.colormaster.androidapp.components.molecules.DrawerMenuList
import net.subroh0508.colormaster.androidapp.components.molecules.MenuListLabel
import net.subroh0508.colormaster.androidapp.components.templates.ModalDrawerScaffold
import net.subroh0508.colormaster.presentation.search.viewmodel.FavoritesViewModel
import net.subroh0508.colormaster.presentation.search.viewmodel.SearchByNameViewModel

private enum class Page(@StringRes override val resId: Int) : MenuListLabel {
    SEARCH(R.string.app_menu_search_attributes),
    FAVORITES(R.string.app_menu_favorites),
    HOW_TO_USE(R.string.app_menu_about_how_to_use),
    DEVELOPMENT(R.string.app_menu_about_development),
    TERMS(R.string.app_menu_about_terms)
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalLayout
@Composable
fun Home(
    searchByNameViewModel: SearchByNameViewModel,
    favoritesViewModel: FavoritesViewModel,
    lifecycleScope: LifecycleCoroutineScope,
    launchPreviewScreen: (ScreenType, List<String>) -> Unit,
) {
    val page = remember { mutableStateOf(Page.SEARCH) }

    ModalDrawerScaffold(
        drawerContent = { drawerState ->
            HomeDrawerContent {
                drawerState.close { page.value = it }
            }
        },
        bodyContent = { drawerState, snackbarHostState ->
            when (page.value) {
                Page.SEARCH -> Search(
                    searchByNameViewModel,
                    lifecycleScope,
                    drawerState,
                    snackbarHostState,
                    launchPreviewScreen,
                )
                Page.FAVORITES -> Favorites(
                    favoritesViewModel,
                    lifecycleScope,
                    drawerState,
                    snackbarHostState,
                    launchPreviewScreen,
                )
            }
        },
        bottomBarHeight = 52.dp,
    )
}

@Composable
private fun HomeDrawerContent(
    onClick: (Page) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        DrawerHeader(
            title = stringResource(R.string.app_name),
            subtext = "v2020.09.20-beta01",
        )
        DrawerMenuList(
            label = stringResource(R.string.app_menu_search_label),
            items = arrayOf(
                Icons.Default.Search to Page.SEARCH,
                Icons.Default.Favorite to Page.FAVORITES,
            ),
            onClick = onClick,
        )
        DrawerMenuList(
            label = stringResource(R.string.app_menu_about_label),
            items = arrayOf(
                Icons.Default.Search to Page.HOW_TO_USE,
                Icons.Default.Search to Page.DEVELOPMENT,
                Icons.Default.Search to Page.TERMS,
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
@ExperimentalMaterialApi
@ExperimentalLayout
fun PreviewHome() {
    //Home(uiModel)
}
