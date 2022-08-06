package components.atoms.backdrop

import MaterialTheme
import androidx.compose.runtime.*
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import material.utilities.rememberMediaQuery
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div

const val WIDE_BACK_LAYER_WIDTH = 408

enum class BackdropValues { Concealed, Revealed }

@Composable
fun Backdrop(
    backdropState: MutableState<BackdropValues> = rememberBackdropState(),
    appBar: @Composable () -> Unit,
    backLayerContent: @Composable () -> Unit,
    frontLayerContent: @Composable () -> Unit,
) {
    val wide by rememberMediaQuery(MEDIA_QUERY_TABLET_SMALL)

    Style(BackdropStyleSheet)

    Div({ classes(BackdropStyleSheet.content) }) {
        Div({ style { width(100.percent) } }) {
            appBar()
            backLayerContent()
        }

        if (!wide) {
            OverlayFrontLayer(backdropState, frontLayerContent)
            return@Div
        }

        Div({ classes(BackdropStyleSheet.frontLayer) }) {
            frontLayerContent()
        }
    }
}

@Composable
fun rememberBackdropState(
    initialValues: BackdropValues = BackdropValues.Concealed,
) = remember(initialValues) { mutableStateOf(initialValues) }

private const val OVERLAY_FRONT_LAYER_CORNER_RADIUS = 16
private const val CONCEALED_FRONT_LAYER_HEIGHT = 64
private const val REVEALED_FRONT_LAYER_TOP = 56

@Composable
private fun OverlayFrontLayer(
    backdropState: MutableState<BackdropValues>,
    frontLayerContent: @Composable () -> Unit,
) {
    val style = remember(backdropState.value) { OverlayFrontLayerStyleSheet(backdropState.value) }

    Style(style)

    Div({ classes(style.content) }) {
        frontLayerContent()
    }
}

private object BackdropStyleSheet : StyleSheet() {
    val content by style {
        display(DisplayStyle.Flex)
        height(100.vh)

        media(MEDIA_QUERY_TABLET_SMALL) {
            type("header") style  {
                width(WIDE_BACK_LAYER_WIDTH.px)
            }
        }
    }

    val frontLayer by style {
        display(DisplayStyle.Flex)
        flexShrink(0)
        width(100.percent - WIDE_BACK_LAYER_WIDTH.px)
        backgroundColor(MaterialTheme.Var.surface)
    }
}

private class OverlayFrontLayerStyleSheet(private val state: BackdropValues) : StyleSheet() {
    val content by style {
        display(DisplayStyle.Flex)
        position(Position.Fixed)
        property("z-index", 1200)
        property("left", 0)
        property("right", 0)
        property("bottom", 0)
        maxHeight(100.percent)
        property("flex", "1 0 auto")
        backgroundColor(MaterialTheme.Var.surface)
        borderRadius(
            topLeft = OVERLAY_FRONT_LAYER_CORNER_RADIUS.px,
            topRight = OVERLAY_FRONT_LAYER_CORNER_RADIUS.px,
            bottomLeft = 0.px,
            bottomRight = 0.px,
        )
        property("transition", "top 375ms cubic-bezier(0, 0, 0.2, 1) 0ms")

        when (state) {
            BackdropValues.Concealed -> {
                height(CONCEALED_FRONT_LAYER_HEIGHT.px)
                property("top", "calc(${100.percent} - ${CONCEALED_FRONT_LAYER_HEIGHT}px)")
            }
            BackdropValues.Revealed -> {
                height(auto)
                property("top", "${REVEALED_FRONT_LAYER_TOP}px")
            }

        }
    }
}
