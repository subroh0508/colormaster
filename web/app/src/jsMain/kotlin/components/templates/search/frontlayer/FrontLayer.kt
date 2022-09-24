package components.templates.search.frontlayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import components.atoms.backdrop.BackdropFrontHeader
import components.atoms.backdrop.BackdropValues
import components.organisms.list.SearchResultList
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import material.utilities.rememberMediaQuery
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import routes.CurrentLocalRouter
import usecase.rememberSearchIdolsUseCase

@Composable
fun FrontLayer(
    backdropState: MutableState<BackdropValues>,
    isSignedIn: Boolean,
    params: SearchParams,
) {
    val router = CurrentLocalRouter() ?: return
    val wide by rememberMediaQuery(MEDIA_QUERY_TABLET_SMALL)
    val idolColorLoadState by rememberSearchIdolsUseCase(params)

    console.log(idolColorLoadState)

    SearchResultList(
        isSignedIn,
        idolColorLoadState,
        openPenlight = router::toPenlight,
        header = { selections, setSelectionsAll ->
            BackdropFrontHeader(backdropState) {
                ActionButtons(
                    wide,
                    backdropState,
                    selections,
                    onSelectAllClick = setSelectionsAll,
                    onOpenPreviewClick = { router.toPreview(selections) },
                    onOpenPenlightClick = { router.toPenlight(selections) },
                )
                LoadStateAlert(
                    params,
                    idolColorLoadState,
                )
            }
        },
        errorContent = {
            ErrorDetail(it)
        }
    )
}
