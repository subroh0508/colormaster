package page

import androidx.compose.runtime.*
import components.atoms.backdrop.Backdrop
import components.atoms.backdrop.BackdropValues
import components.atoms.backdrop.rememberBackdropState
import components.templates.search.BackLayer
import components.templates.search.frontlayer.FrontLayer
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import usecase.rememberSearchIdolsUseCase

@Composable
fun SearchIdolPage(
    variant: String,
    isSignedIn: Boolean,
    appBar: @Composable (String) -> Unit,
) {
    val backdropState = rememberBackdropState(BackdropValues.Concealed)
    val (params, setParams) = remember { mutableStateOf<SearchParams?>(null) }
    val idolLoadState by rememberSearchIdolsUseCase(params)

    Backdrop(
        backdropState,
        appBar = { appBar(variant) },
        backLayerContent = {
            BackLayer(variant, setParams)
        },
        frontLayerContent = {
            FrontLayer(backdropState, isSignedIn, params, idolLoadState)
        },
    )
}
