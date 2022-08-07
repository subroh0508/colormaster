package components.templates.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import components.atoms.alert.Alert
import components.atoms.alert.AlertType
import components.atoms.backdrop.BackdropFrontHeader
import components.atoms.backdrop.BackdropValues
import page.SearchUiModel
import utilities.LocalI18n
import utilities.invoke

@Composable
fun FrontLayer(
    backdropState: MutableState<BackdropValues>,
    model: SearchUiModel,
) {
    val t = LocalI18n() ?: return

    console.log(model)

    BackdropFrontHeader(backdropState) {
        Alert(
            AlertType.Info,
            t("searchPanel.messages.defaultByName"),
        )
    }
}
