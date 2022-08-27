package components.templates.search.frontlayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import components.atoms.backdrop.BackdropFrontHeader
import components.atoms.backdrop.BackdropValues
import components.organisms.list.SearchResultList
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import material.utilities.rememberMediaQuery
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.search.model.SearchParams

@Composable
fun FrontLayer(
    backdropState: MutableState<BackdropValues>,
    params: SearchParams?,
    loadState: LoadState,
) {
    val wide by rememberMediaQuery(MEDIA_QUERY_TABLET_SMALL)

    console.log(loadState)

    SearchResultList(
        loadState,
        header = { selections, setSelectionsAll ->
            BackdropFrontHeader(backdropState) {
                ActionButtons(
                    wide,
                    backdropState,
                    selections,
                    onSelectAllClick = setSelectionsAll,
                )
                LoadStateAlert(
                    params,
                    loadState,
                )
            }
        },
    )
}
