package page

import androidx.compose.runtime.*
import components.atoms.backdrop.Backdrop
import components.atoms.backdrop.BackdropValues
import components.atoms.backdrop.rememberBackdropState
import components.templates.search.BackLayer
import components.templates.search.frontlayer.FrontLayer
import material.components.TopAppBarVariant
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import usecase.rememberSearchIdolsUseCase

@Composable
fun SearchIdolPage(
    topAppBarVariant: String,
    isSignedIn: Boolean,
) {
    val backdropState = rememberBackdropState(BackdropValues.Concealed)
    val (params, setParams) = remember { mutableStateOf<SearchParams?>(null) }

    Backdrop(
        backdropState,
        backLayerContent = {
            BackLayer(topAppBarVariant, setParams)
        },
        frontLayerContent = {
            FrontLayer(backdropState, isSignedIn, params)
        },
    )
}
