package components.atoms

import kotlinx.css.*
import kotlinx.css.properties.BoxShadows
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarColor
import materialui.components.appbar.enums.AppBarPosition
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.palette.paper
import react.*
import react.dom.div

val APP_BAR_SM_UP = 408.px

fun RBuilder.appFrame(handler: RHandler<AppFrameProps>) = child(AppFrameComponent, handler = handler)

private val AppFrameComponent = functionalComponent<AppFrameProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        appBar {
            attrs {
                classes(classes.appBar)
                color = AppBarColor.default
                position = AppBarPosition.fixed
            }

            props.ToolbarComponent?.let(::child)
        }
    }

    props.children()
}

external interface AppFrameProps : RProps {
    @Suppress("PropertyName")
    var ToolbarComponent: ReactElement?
}

@Suppress("FunctionName")
fun AppFrameProps.ToolbarComponent(block: RBuilder.() -> Unit) {
    ToolbarComponent = buildElement(block)
}

private external interface AppFrameStyle {
    val root: String
    val appBar: String
}

private val useStyles = makeStyles<AppFrameStyle> {
    "root" {
        flexGrow = 1.0
    }
    "appBar" {
        zIndex = theme.zIndex.drawer.toInt() + 1
        backgroundColor = theme.palette.background.paper
        boxShadow = BoxShadows.none

        (theme.breakpoints.up(Breakpoint.sm)) {
            left = 0.px
            width = APP_BAR_SM_UP
        }
    }
}
