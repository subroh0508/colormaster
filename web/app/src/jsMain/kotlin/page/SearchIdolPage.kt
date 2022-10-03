package page

import androidx.compose.runtime.*
import components.atoms.backdrop.Backdrop
import components.atoms.backdrop.BackdropValues
import components.atoms.backdrop.rememberBackdropState
import components.templates.search.BackLayer
import components.templates.search.frontlayer.FrontLayer
import material.components.TopAppBarVariant
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import routes.Search
import usecase.rememberSearchIdolsUseCase

@Composable
fun SearchIdolPage(
    topAppBarVariant: String,
    isSignedIn: Boolean,
    initParams: SearchParams,
) {
    val backdropState = rememberBackdropState(BackdropValues.Concealed)
    val (params, setParams) = remember(initParams) { mutableStateOf(initParams) }

    val tab = when (initParams) {
        is SearchParams.ByName -> SearchByTab.BY_NAME
        is SearchParams.ByLive -> SearchByTab.BY_LIVE
        else -> SearchByTab.BY_NAME
    }

    Backdrop(
        backdropState,
        backLayerContent = {
            BackLayer(topAppBarVariant, tab, setParams)
        },
        frontLayerContent = {
            FrontLayer(backdropState, isSignedIn, params)
        },
    )
}
