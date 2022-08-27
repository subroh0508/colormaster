package components.atoms.backdrop

import MaterialTheme
import androidx.compose.runtime.*
import material.components.IconButton
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import material.utilities.rememberMediaQuery
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div

const val WIDE_BACK_LAYER_WIDTH = 408
const val TOP_APP_BAR_HEIGHT = 64
const val BACKDROP_FRONT_HEADER_HEIGHT = 68

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
        appBar()
        backLayerContent()

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
fun BackdropFrontHeader(
    backdropState: MutableState<BackdropValues>,
    content: @Composable () -> Unit,
) {
    val wide by rememberMediaQuery(MEDIA_QUERY_TABLET_SMALL)

    Style(BackdropFrontHeaderStyleSheet)

    Div({ classes(BackdropFrontHeaderStyleSheet.content) }) {
        content()

        if (!wide) {
            val icon = "expand_${if (backdropState.value == BackdropValues.Revealed) "more" else "less"}"

            IconButton(icon) {
                classes(BackdropFrontHeaderStyleSheet.icon)
                onClick {
                    backdropState.value = when (backdropState.value) {
                        BackdropValues.Revealed -> BackdropValues.Concealed
                        BackdropValues.Concealed -> BackdropValues.Revealed
                    }
                }
            }
        }
    }
}

@Composable
fun rememberBackdropState(
    initialValues: BackdropValues = BackdropValues.Concealed,
) = remember(initialValues) { mutableStateOf(initialValues) }

private const val FRONT_LAYER_BORDER_RADIUS = 16
private const val CONCEALED_FRONT_LAYER_HEIGHT = 68
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
        overflowY("hidden")
    }

    val frontLayer by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        flexShrink(0)
        width(100.percent - WIDE_BACK_LAYER_WIDTH.px)
        backgroundColor(MaterialTheme.Var.surface)

        media(MEDIA_QUERY_TABLET_SMALL) {
            self style {
                marginTop(TOP_APP_BAR_HEIGHT.px)
                frontLayerBorderRadius(wide = true)
            }
        }
    }
}

private object BackdropFrontHeaderStyleSheet : StyleSheet() {
    val content by style {
        position(Position.Relative)
        padding(8.px)

        media(MEDIA_QUERY_TABLET_SMALL) {
            self style {
                padding(8.px, 0.px, 8.px, 8.px)
            }
        }
    }

    val icon by style {
        position(Position.Absolute)
        property("right", 0)
        property("bottom", 0)
        property("margin", "auto 10px 10px auto")
        color(MaterialTheme.Var.onSurface)
    }
}

private class OverlayFrontLayerStyleSheet(private val state: BackdropValues) : StyleSheet() {
    val content by style {
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        position(Position.Fixed)
        property("z-index", 4)
        property("left", 0)
        property("right", 0)
        property("bottom", 0)
        maxHeight(100.percent)
        property("flex", "1 0 auto")
        backgroundColor(MaterialTheme.Var.surface)
        frontLayerBorderRadius(wide = false)
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

private fun CSSBuilder.frontLayerBorderRadius(wide: Boolean) = borderRadius(
    topLeft = FRONT_LAYER_BORDER_RADIUS.px,
    topRight = (if (wide) 0 else FRONT_LAYER_BORDER_RADIUS).px,
    bottomLeft = 0.px,
    bottomRight = 0.px,
)
