package components.atoms

import kotlinx.css.*
import kotlinx.css.properties.BoxShadows
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarColor
import materialui.components.appbar.enums.AppBarPosition
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.palette.default
import react.*
import react.dom.div

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

    div(classes.toolbar) {}
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
    val toolbar: String
}

private val useStyles = makeStyles<AppFrameStyle> {
    "root" {
        flexGrow = 1.0
    }
    "appBar" {
        zIndex = 0
        backgroundColor = theme.palette.background.default
        boxShadow = BoxShadows.none
    }
    "toolbar"(theme.mixins.toolbar)
}
