package components.atoms

import kotlinx.css.*
import kotlinx.css.properties.border
import kotlinx.css.properties.borderTop
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
import materialui.styles.muitheme.spacing
import materialui.styles.palette.paper
import materialui.styles.transitions.create
import materialui.styles.transitions.easeOut
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
    val rootStyle = "${classes.root} ${if (props.opened) classes.open else classes.close}"
    val headerStyle = "${classes.header} ${if (props.opened) classes.headerOpen else classes.headerClose}"
    val contentStyle = "${classes.content} ${if (props.opened) classes.contentOpen else classes.contentClose}"

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

            div(contentStyle) {
                props.children()
            }
        }
    }
}

private val HiddenXsDown = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()
    val rootStyle = "${classes.root} ${classes.open}"
    val headerStyle = "${classes.header} ${if (props.opened) classes.headerOpen else classes.headerClose}"
    val contentStyle = "${classes.content} ${if (props.opened) classes.contentOpen else classes.contentClose}"

    hidden {
        attrs {
            xsDown = true
        }

        drawer(
            DrawerStyle.root to rootStyle,
            DrawerStyle.paper to rootStyle
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
    val root: String
    val open: String
    val close: String
    val header: String
    val headerOpen: String
    val headerClose: String
    val content: String
    val contentOpen: String
    val contentClose: String
    val expandIcon: String
}

private val useStyles = makeStyles<ResponsiveDrawerStyle> {
    val offsetTop = CSSBuilder().apply {
        top = 56.px
        media("(min-width:0px) and (orientation: landscape)") {
            top = 48.px
        }
        (theme.breakpoints.up(Breakpoint.sm)) {
            top = 64.px
        }
    }

    "root" {
        border = "none"
        backgroundColor = Color.transparent
    }

    "open"(offsetTop.apply {
        (theme.breakpoints.up(Breakpoint.sm)) {
            top = 0.px
            width = DRAWER_WIDTH_SM_UP
            flexShrink = 0.0
        }
    })
    "close" {
        height = DRAWER_HEADER_HEIGHT_XM_UP
    }
    "header" {
        position = Position.fixed
        left = 0.px
        right = 0.px
        zIndex = 1
        padding(8.px, 8.px)
        backgroundColor = theme.palette.background.paper
        borderTopLeftRadius = 16.px
        borderTopRightRadius = 16.px

        (theme.breakpoints.up(Breakpoint.sm)) {
            padding(16.px, 8.px)
            borderTopLeftRadius = 0.px
            borderTopRightRadius = 0.px
        }
    }
    "headerOpen"(offsetTop.apply {
        transition = theme.transitions.create("top") {
            easing = theme.transitions.easing.easeOut
            duration = theme.transitions.duration.complex
        }

        (theme.breakpoints.up(Breakpoint.sm)) {
            top = 0.px
            width = DRAWER_WIDTH_SM_UP
            margin(0.px, (-1).px, 0.px, LinearDimension.auto)
        }
    })
    "headerClose" {
        top = 100.pct - DRAWER_HEADER_HEIGHT_XM_UP
        bottom = 0.px

        transition = theme.transitions.create("top") {
            easing = theme.transitions.easing.easeOut
            duration = theme.transitions.duration.complex
        }
    }
    "content" {
        position = Position.fixed
        left = 0.px
        right = 0.px
        paddingTop = DRAWER_HEADER_HEIGHT_XM_UP
        backgroundColor = theme.palette.background.paper
        borderTopLeftRadius = 16.px
        borderTopRightRadius = 16.px

        (theme.breakpoints.up(Breakpoint.sm)) {
            paddingTop = DRAWER_HEADER_HEIGHT_SM_UP
            borderTopLeftRadius = 0.px
            borderTopRightRadius = 0.px
        }
    }
    "contentOpen"(offsetTop.apply {
        height = 100.pct
        overflowY = Overflow.auto

        transition = theme.transitions.create("top") {
            easing = theme.transitions.easing.easeOut
            duration = theme.transitions.duration.complex
        }

        (theme.breakpoints.up(Breakpoint.sm)) {
            top = 0.px
            width = DRAWER_WIDTH_SM_UP
            margin(0.px, (-1).px, 0.px, LinearDimension.auto)
            paddingTop = DRAWER_HEADER_HEIGHT_SM_UP
        }
    })
    "contentClose" {
        top = 100.pct - DRAWER_HEADER_HEIGHT_XM_UP
        height = 0.px
        transition = theme.transitions.create("top") {
            easing = theme.transitions.easing.easeOut
            duration = theme.transitions.duration.complex
        }
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
