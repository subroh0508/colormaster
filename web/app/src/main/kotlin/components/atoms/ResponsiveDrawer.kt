package components.atoms

import kotlinext.js.js
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.hidden.hidden
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import materialui.components.modal.ModalProps
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.muitheme.spacing
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val DRAWER_WIDTH = 408.px

fun RBuilder.responsiveDrawer(handler: RHandler<ResponsiveDrawerProps>) {
    child(HiddenSmUp, handler = handler)
    child(HiddenXsDown, handler = handler)
}

private val HiddenSmUp = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()

    hidden {
        attrs { smUp = true }

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

private val HiddenXsDownX = functionalComponent<ResponsiveDrawerProps> { props ->
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

private val HiddenXsDown = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()

    hidden {
        attrs {
            xsDown = true
        }

        drawer(
            DrawerStyle.root to classes.open,
            DrawerStyle.paper to classes.open
        ) {
            attrs {
                variant = DrawerVariant.persistent
                open = true
                anchor = props.anchor
            }

            div(classes.header) {
                props.HeaderComponent?.let(::child)
            }
            props.children()
        }
    }
}

private val HiddenSmUpX = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()
    val (opened, openDrawer) = useState(false)

    hidden {
        attrs {
            smUp = true
        }

        drawer(
            DrawerStyle.root to classes.close,
            DrawerStyle.paper to classes.close
        ) {
            attrs {
                variant = DrawerVariant.persistent
                open = !opened
                anchor = props.anchor
                onClose = { props.onClose() }
            }

            div(classes.toolbar) {}
            iconButton {
                attrs {
                    classes(classes.expandIcon)
                    icon { +"chevron_${if (props.anchor == DrawerAnchor.left) "right" else "left"}_icon" }

                    onClickFunction = { props.onClose() }
                }
            }
        }

        drawer(
            DrawerStyle.root to classes.open,
            DrawerStyle.paper to classes.open
        ) {
            attrs {
                open = opened
                anchor = props.anchor
                onClose = { openDrawer(false) }
                ModalProps = jsObject<ModalProps> {
                    setProp("style", js {
                        this["zIndex"] = 1200
                    } as Any)
                }
            }

            div(classes.toolbar) {}
            props.children()
        }
    }
}

external interface ResponsiveDrawerProps : RProps {
    var anchor: DrawerAnchor
    var opened: Boolean
    @Suppress("PropertyName")
    var HeaderComponent: ReactElement?
    var onClose: () -> Unit
}

@Suppress("FunctionName")
fun ResponsiveDrawerProps.HeaderComponent(block: RBuilder.() -> Unit) {
    HeaderComponent = buildElement(block)
}

private external interface ResponsiveDrawerStyle {
    val open: String
    val close: String
    val header: String
    val toolbar: String
    val expandIcon: String
}

private val useStyles = makeStyles<ResponsiveDrawerStyle> {
    "open" {
        width = DRAWER_WIDTH
        flexShrink = 0.0
    }
    "close" {
        width = theme.spacing(6)
        flexShrink = 0.0
    }
    "header" {
        height = 64.px
        // height = theme.mixins.toolbar.height <- ClassCastException
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
    }
    "toolbar"(theme.mixins.toolbar)
    "expandIcon" {
        width = theme.spacing(3)
        margin(theme.spacing(1), LinearDimension.auto, theme.spacing(1), 12.px)
    }
}
