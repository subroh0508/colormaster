package pages

import materialui.components.container.container
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import react.*
import react.dom.div

@Suppress("FunctionName")
fun RBuilder.StaticPage(handler: RHandler<RProps>) = child(StaticPageComponent, handler = handler)

private val StaticPageComponent = functionalComponent<RProps> { props ->
    val classes = useStyles()

    div(classes.toolbar) {}
    container { props.children() }
}

private external interface StaticPageStyle {
    val root: String
    val toolbar: String
}

private val useStyles = makeStyles<StaticPageStyle> {
    "toolbar"(theme.mixins.toolbar)
}
