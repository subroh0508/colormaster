package components.atoms

import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.hidden.hidden
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.muitheme.spacing
import react.*
import react.dom.div

fun RBuilder.responsiveDrawer(handler: RHandler<ResponsiveDrawerProps>) {
    child(HiddenSmUp, handler = handler)
    child(HiddenXsDown, handler = handler)
}

private val HiddenXsDown = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()

    hidden {
        attrs { xsDown = true }

        drawer(
            DrawerStyle.root to classes.open,
            DrawerStyle.paper to classes.open
        ) {
            attrs {
                variant = DrawerVariant.permanent
                anchor = props.anchor
            }

            div(classes.toolbar) {}

            props.children()
        }
    }
}

private val HiddenSmUp = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()
    val (opened, openDrawer) = useState(false)

    val direction = when {
        props.anchor == DrawerAnchor.right && opened -> "right"
        props.anchor == DrawerAnchor.right && !opened -> "left"
        props.anchor == DrawerAnchor.left && opened -> "left"
        props.anchor == DrawerAnchor.left && !opened -> "right"
        else -> throw IllegalStateException()
    }

    hidden {
        attrs { smUp = true }

        drawer(
            DrawerStyle.root to if (opened) classes.open else classes.close,
            DrawerStyle.paper to if (opened) classes.open else classes.close
        ) {
            attrs {
                variant = DrawerVariant.permanent
                anchor = props.anchor
            }

            div(classes.toolbar) {}
            iconButton {
                attrs {
                    classes(classes.expandIcon)
                    icon { +"chevron_${direction}_icon" }

                    onClickFunction = { openDrawer(!opened) }
                }
            }

            if (opened) {
                props.children()
            }
        }
    }
}

external interface ResponsiveDrawerProps : RProps {
    var anchor: DrawerAnchor
}

private external interface ResponsiveDrawerStyle {
    val open: String
    val close: String
    val toolbar: String
    val expandIcon: String
}

private val DRAWER_WIDTH = 240.px

private val useStyles = makeStyles<ResponsiveDrawerStyle> {
    "open" {
        width = DRAWER_WIDTH
        flexShrink = 0.0
    }
    "close" {
        width = theme.spacing(6)
        flexShrink = 0.0
    }
    css["toolbar"] = theme.mixins.toolbar
    "expandIcon" {
        width = theme.spacing(3)
        margin(theme.spacing(1), LinearDimension.auto, theme.spacing(1), 12.px)
    }
}
