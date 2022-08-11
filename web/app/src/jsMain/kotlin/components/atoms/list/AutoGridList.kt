package components.atoms.list

import androidx.compose.runtime.*
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import material.utilities.rememberMediaQuery
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement
import utilities.ResizeObserver
import utilities.ResizeObserverEntry

@Composable
fun AutoGridList(
    gridMinWidth: Int,
    marginHorizontal: Int,
    attrsScope: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    content: @Composable (Float) -> Unit,
) {
    val wide by rememberMediaQuery(MEDIA_QUERY_TABLET_SMALL)
    val (ref, setRef) = remember { mutableStateOf<HTMLDivElement?>(null) }
    val (width, setWidth) = remember(ref, gridMinWidth, marginHorizontal) { mutableStateOf(gridMinWidth.toFloat()) }

    DisposableEffect(ref, wide, gridMinWidth, marginHorizontal) {
        if (!wide) {
            return@DisposableEffect onDispose {  }
        }

        val observer = ResizeObserver { entries, _ ->
            setWidth(calcGridWidth(entries, gridMinWidth, marginHorizontal))
        }

        ref?.let { observer.observe(it) }

        onDispose { ref?.let { observer.unobserve(it) } }
    }

    Div({
        attrsScope?.invoke(this)

        ref {
            setRef(it)

            onDispose { setRef(null) }
        }
    }) { content(width) }
}

private fun calcGridWidth(
    entries: Array<ResizeObserverEntry>,
    gridMinWidth: Int,
    marginHorizontal: Int,
) = entries[0].contentBoxSize[0].inlineSize.toInt().let { contentWidth ->
    val columns = contentWidth / (gridMinWidth + marginHorizontal)

    (contentWidth.toFloat() / columns.toFloat()) - marginHorizontal.toFloat()
}
