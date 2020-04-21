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
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.muitheme.spacing
import react.*
import react.dom.div

val DRAWER_HEADER_HEIGHT_XM_UP = 64.px
val DRAWER_HEADER_HEIGHT_SM_UP = 80.px
val DRAWER_WIDTH_SM_UP = 100.pct - 408.px

fun RBuilder.responsiveDrawer(handler: RHandler<ResponsiveDrawerProps>) {
    child(HiddenSmUp, handler = handler)
    child(HiddenXsDown, handler = handler)
}

private val HiddenSmUp = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()
    val rootStyle = if (props.opened) classes.open else classes.close
    val headerStyle = "${classes.header} ${if (props.opened) classes.headerOpen else classes.headerClose}"
    val contentStyle = if (props.opened) classes.contentOpen else classes.contentClose

    hidden {
        attrs { smUp = true }

        drawer(
            DrawerStyle.root to rootStyle,
            DrawerStyle.paper to rootStyle
        ) {
            attrs {
                variant = DrawerVariant.permanent
                anchor = props.anchor
                open = props.opened
                onClose = { props.onClose }
            }

            props.HeaderComponent?.let {
                div(headerStyle) {
                    if (props.opened) {
                        div(classes.toolbar) {}
                    }

                    child(it)

                    iconButton {
                        attrs {
                            classes(classes.expandIcon)
                            icon { +"expand_${if (props.opened) "more" else "less"}_icon" }

                            onClickFunction = { props.onClickExpandIcon() }
                        }
                    }
                }
            }

            div(classes.toolbar) {}
            div(contentStyle) {
                props.children()
            }
        }
    }
}

private val HiddenXsDown = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()
    val headerStyle = "${classes.header} ${if (props.opened) classes.headerOpen else classes.headerClose}"
    val contentStyle = if (props.opened) classes.contentOpen else classes.contentClose

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

            props.HeaderComponent?.let {
                div(headerStyle) {
                    child(it)
                }
            }

            div(contentStyle) {
                props.children()
            }
        }
    }
}

external interface ResponsiveDrawerProps : RProps {
    var anchor: DrawerAnchor
    var opened: Boolean
    @Suppress("PropertyName")
    var HeaderComponent: ReactElement?
    var onClose: () -> Unit
    var onClickExpandIcon: () -> Unit
}

@Suppress("FunctionName")
fun ResponsiveDrawerProps.HeaderComponent(block: RBuilder.() -> Unit) {
    HeaderComponent = buildElement(block)
}

private external interface ResponsiveDrawerStyle {
    val open: String
    val close: String
    val toolbar: String
    val header: String
    val headerOpen: String
    val headerClose: String
    val contentOpen: String
    val contentClose: String
    val expandIcon: String
}

private val useStyles = makeStyles<ResponsiveDrawerStyle> {
    "open" {
        (theme.breakpoints.up(Breakpoint.sm)) {
            width = DRAWER_WIDTH_SM_UP
            flexShrink = 0.0
        }
    }
    "close" {
        height = DRAWER_HEADER_HEIGHT_XM_UP
    }
    "toolbar"(theme.mixins.toolbar.apply {
        backgroundColor = Color.transparent
    })
    "header" {
        position = Position.fixed
        left = 0.px
        right = 0.px
        zIndex = 1
        backgroundColor = Color.white
    }
    "headerOpen" {
        top = 0.px

        (theme.breakpoints.up(Breakpoint.sm)) {
            width = DRAWER_WIDTH_SM_UP
            padding(8.px, 0.px)
            margin(0.px, (-1).px, 0.px, LinearDimension.auto)
        }
    }
    "headerClose" {
        bottom = 0.px
    }
    "contentOpen" {
        height = 100.vh
        marginTop = DRAWER_HEADER_HEIGHT_XM_UP

        (theme.breakpoints.up(Breakpoint.sm)) {
            marginTop = DRAWER_HEADER_HEIGHT_SM_UP
        }
    }
    "contentClose" {
        display = Display.none
    }
    "expandIcon" {
        position = Position.absolute
        right = 0.px
        bottom = 0.px
        width = theme.spacing(3)
        margin(LinearDimension.auto, theme.spacing(2), theme.spacing(1), LinearDimension.auto)
        zIndex = 1
    }
}
