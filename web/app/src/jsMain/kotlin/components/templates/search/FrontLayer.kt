package components.templates.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import components.atoms.alert.Alert
import components.atoms.alert.AlertType
import components.atoms.backdrop.BackdropFrontHeader
import components.atoms.backdrop.BackdropValues
import components.organisms.list.SearchResultList
import net.subroh0508.colormaster.presentation.common.LoadState
import utilities.LocalI18n
import utilities.invoke

@Composable
fun FrontLayer(
    backdropState: MutableState<BackdropValues>,
    loadState: LoadState,
) {
    val t = LocalI18n() ?: return

    console.log(loadState)

    BackdropFrontHeader(backdropState) {
        Alert(
            AlertType.Info,
            t("searchPanel.messages.defaultByName"),
        )
    }

    SearchResultList(loadState)
}
