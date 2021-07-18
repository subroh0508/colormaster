package pages

import kotlinx.css.*
import materialui.components.container.container
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.muitheme.spacing
import materialui.styles.palette.default
import react.*
import react.dom.div

@Suppress("FunctionName")
fun RBuilder.StaticPage(handler: RHandler<RProps>) = child(StaticPageComponent, handler = handler)

private val StaticPageComponent = functionComponent<RProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        div(classes.toolbar) {}
        container {
            attrs.classes(classes.container)
            props.children()
        }
    }
}

const val CARD_ELEMENT_CLASS_NAME = "card-element"

private external interface StaticPageStyle {
    val root: String
    val toolbar: String
    val container: String
}

private val useStyles = makeStyles<StaticPageStyle> {
    "root" {
        backgroundColor = theme.palette.background.default
    }
    "toolbar"(theme.mixins.toolbar)
    "container" {
        paddingTop = theme.spacing(2)

        descendants(".$CARD_ELEMENT_CLASS_NAME") {
            marginBottom = theme.spacing(4)
        }
    }
}
