package components.atoms

import kotlinx.html.js.onClickFunction
import net.subroh0508.colormaster.utilities.currentTimeMillis
import react.*
import react.dom.div
import kotlin.browser.window

const val CLICK_HANDLER_CLASS_NAME = "click-handler"

fun RBuilder.clickHandler(handler: RHandler<ClickHandlerProps>) = child(ClickHandlerComponent, handler = handler)

private val ClickHandlerComponent = functionalComponent<ClickHandlerProps> { props ->
    val lastTimeRef = useRef(0L)

    div(CLICK_HANDLER_CLASS_NAME) {
        attrs {
            onClickFunction = {
                handleOnClick(
                    props.doubleClickDurationMillis ?: DEFAULT_DOUBLE_CLICK_DURATION_MILLIS,
                    lastTimeRef,
                    props.onClick,
                    props.onDoubleClick
                )
            }

        }

        props.children()
    }
}

private const val DEFAULT_DOUBLE_CLICK_DURATION_MILLIS = 200

private fun handleOnClick(
    durationMillis: Int,
    lastTimeRef: RMutableRef<Long>,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit
) {
    val currentTime = currentTimeMillis()

    if (lastTimeRef.current != 0L && currentTime - lastTimeRef.current < durationMillis) {
        onDoubleClick()
        lastTimeRef.current = 0L

        return
    }

    lastTimeRef.current = currentTime
    window.setTimeout({
        if (lastTimeRef.current != 0L) {
            onClick()
        }

        lastTimeRef.current = 0L
    }, durationMillis)
}

external interface ClickHandlerProps : RProps {
    var onClick: () -> Unit
    var onDoubleClick: () -> Unit
    var doubleClickDurationMillis: Int?
}
