package components.molecules

import components.atoms.COLOR_PREVIEW_ITEM_CLASS_NAME
import components.atoms.clickHandler
import components.atoms.colorPreviewItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.js.*
import materialui.components.icon.icon
import materialui.components.paper.paper
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.common.throttleFirst
import react.*
import styled.ReactModule
import styled.animation
import utilities.isMobile

fun RBuilder.colorGridItem(handler: RHandler<ColorGridItem>) = child(ColorGridItemComponent, handler = handler)

private val ColorGridItemComponent = memo(functionalComponent<ColorGridItem> { props ->
    val classes = useStyles(props)
    val (mouse, setMouseEvent) = useState(Mouse.NONE)

    val handleOnClick = useCallback(props.onClick, arrayOf(props.item.id))
    val handleOnDoubleClick = useCallback(props.onDoubleClick, arrayOf(props.item.id))

    val channel = throttleFirstMouseEventChannel(100) { setMouseEvent(it) }

    fun offerMouseEvent(previous: Mouse, next: Mouse) {
        if (previous == Mouse.CLICK || next == Mouse.CLICK) channel?.trySend(next) else setMouseEvent(next)
    }

    paper {
        attrs {
            classes(rootStyle(classes, mouse, props.isSelected))

            onClickFunction = { offerMouseEvent(mouse, Mouse.CLICK) }
            onMouseOverFunction = { offerMouseEvent(mouse, Mouse.OVER) }
            onMouseOutFunction = { offerMouseEvent(mouse, Mouse.OUT) }
        }

        if (props.isSelected) {
            icon {
                attrs.classes(classes.checkIcon)
                +"check_circle_outline_icon"
            }
        }

        clickHandler {
            key = props.item.id

            attrs {
                onClick = { handleOnClick(props.item, !props.isSelected) }
                onDoubleClick = { handleOnDoubleClick(props.item) }
            }

            colorPreviewItem {
                attrs {
                    name = props.item.name
                    color = props.item.color
                    isBrighter = props.item.isBrighter
                }
            }
        }
    }
})

private inline fun throttleFirstMouseEventChannel(
    durationMillis: Long,
    crossinline action: suspend (Mouse) -> Unit
): Channel<Mouse>? {
    val scope = useRef(CoroutineScope(Job()))

    return useRef(Channel<Mouse>().apply {
        scope.current?.launch {
            consumeAsFlow()
                .throttleFirst(durationMillis)
                .collect { action.invoke(it) }
        }
    }).current
}

external interface ColorGridItem : RProps {
    var item: IdolColor
    var isSelected: Boolean
    var onClick: (IdolColor, Boolean) -> Unit
    var onDoubleClick: (IdolColor) -> Unit
}

private external interface ColorGridStyle {
    val root: String
    val small: String
    val mouseOver: String
    val mouseOut: String
    val checkIcon: String
}

private enum class Mouse {
    NONE, OVER, OUT, CLICK
}

private val useStyles = makeStyles<ColorGridStyle, ColorGridItem> {
    dynamic("root") { props ->
        width = 100.pct
        color = if (props.item.isBrighter) Color.black else Color.white
        margin(4.px)

        descendants(".$COLOR_PREVIEW_ITEM_CLASS_NAME") {
            height = 50.px
            borderRadius = theme.shape.borderRadius.px
        }
    }
    "small" {
        transform.scale(0.9)
    }
    "mouseOver" {
        // Workaround
        if (js("process.env.NODE_ENV === 'production'")) {
            animation(0.1.s, fillMode = FillMode.forwards) {
                100 { transform.scale(0.9) }
            }
        }
    }
    "mouseOut" {
        // Workaround
        if (js("process.env.NODE_ENV === 'production'")) {
            animation(0.1.s, fillMode = FillMode.forwards) {
                0 { transform.scale(0.9) }
                100 { transform.scale(1.0) }
            }
        }
    }
    "checkIcon" {
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        margin(LinearDimension.auto, 4.px)
    }
}

private fun rootStyle(classes: ColorGridStyle, mouse: Mouse, isSelected: Boolean): String {
    if (isMobile) {
        return when (isSelected) {
            true -> "${classes.root} ${classes.mouseOver}"
            false -> "${classes.root} ${classes.mouseOut}"
        }
    }

    if (isSelected) {
        return "${classes.root} ${classes.small}"
    }

    return when (mouse) {
        Mouse.OVER -> "${classes.root} ${classes.mouseOver}"
        Mouse.OUT -> "${classes.root} ${classes.mouseOut}"
        Mouse.CLICK -> "${classes.root} ${classes.small}"
        else -> classes.root
    }
}
