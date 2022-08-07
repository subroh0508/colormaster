package page

import androidx.compose.runtime.Composable
import components.atoms.alert.Alert
import components.atoms.alert.AlertType
import components.atoms.backdrop.Backdrop
import components.atoms.backdrop.BackdropFrontHeader
import components.atoms.backdrop.BackdropValues
import components.atoms.backdrop.rememberBackdropState
import components.templates.search.BackLayer
import material.components.IconButton
import material.components.TopAppBarMainContent
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import utilities.LocalI18n
import utilities.invoke

@Composable
fun SearchIdolPage(
    variant: String,
    appBar: @Composable (String) -> Unit,
) {
    val t = LocalI18n() ?: return
    val backdropState = rememberBackdropState(BackdropValues.Concealed)

    Backdrop(
        backdropState,
        appBar = { appBar(variant) },
        backLayerContent = { BackLayer(variant) },
        frontLayerContent = {
            BackdropFrontHeader(backdropState) {
                Alert(
                    AlertType.Info,
                    t("searchPanel.messages.defaultByName"),
                )
            }
        },
    )
}
