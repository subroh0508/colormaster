package page

import androidx.compose.runtime.Composable
import components.atoms.backdrop.Backdrop
import components.atoms.backdrop.BackdropValues
import components.atoms.backdrop.rememberBackdropState
import material.components.IconButton
import material.components.TopAppBarMainContent
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun SearchIdolPage(
    variant: String,
    appBar: @Composable (String) -> Unit,
) {
    val backdropState = rememberBackdropState(BackdropValues.Concealed)

    Backdrop(
        backdropState,
        appBar = { appBar(variant) },
        backLayerContent = {
            TopAppBarMainContent(variant) {
                Div({ style { color(Color.white) } }) { Text("back layer") }
            }
        },
        frontLayerContent = {
            Div({ style { color(Color.white); width(100.percent) } }) {
                Text("front layer")
                IconButton("expand_more") {
                    onClick {
                        backdropState.value = when (backdropState.value) {
                            BackdropValues.Revealed -> BackdropValues.Concealed
                            BackdropValues.Concealed -> BackdropValues.Revealed
                        }
                    }
                }
            }
        },
    )
}
