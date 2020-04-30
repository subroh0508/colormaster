package components.atoms

import kotlinx.css.Color
import kotlinx.css.color
import materialui.components.link.link as muiLink
import materialui.styles.makeStyles
import react.*

fun RBuilder.link(href: String, label: String, target: String = "_blank") = child(LinkComponent) {
    attrs.href = href
    attrs.target = target
    +label
}

fun RBuilder.link(href: String, target: String = "_blank", block: RBuilder.() -> Unit) = child(LinkComponent) {
    attrs.href = href
    attrs.target = target
    block()
}

private val LinkComponent = functionalComponent<LinkProps> { props ->
    val classes = useStyle()

    muiLink {
        attrs.classes(classes.root)
        attrs.href = props.href
        attrs.target = props.target

        props.children()
    }
}

private external interface LinkProps : RProps {
    var href: String
    var target: String
}

private external interface LinkStyle {
    val root: String
}

private val useStyle = makeStyles<LinkStyle> {
    "root" {
        color = Color("#00B5E2")
    }
}
