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
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.muitheme.spacing
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val DRAWER_HEIGHT_CLOSE_XM_UP = 64.px
val DRAWER_WIDTH_SM_UP = 100.pct - 408.px

fun RBuilder.responsiveDrawer(handler: RHandler<ResponsiveDrawerProps>) {
    child(HiddenSmUp, handler = handler)
    child(HiddenXsDown, handler = handler)
}

private val HiddenSmUp = functionalComponent<ResponsiveDrawerProps> { props ->
    val classes = useStyles()
    val rootStyle = if (props.opened) classes.open else classes.close

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

            if (props.opened) {
                div(classes.toolbar) {}
            }

            iconButton {
                attrs {
                    classes(classes.expandIcon)
                    icon { +"expand_${if (props.opened) "more" else "less"}_icon" }

                    onClickFunction = { props.onClickExpandIcon() }
                }
            }

            div(classes.drawerContent) {
                props.children()
            }
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

            div(classes.drawerContent) {
                props.children()
            }
        }
    }
}

external interface ResponsiveDrawerProps : RProps {
    var anchor: DrawerAnchor
    var opened: Boolean
    var onClose: () -> Unit
    var onClickExpandIcon: () -> Unit
}

private external interface ResponsiveDrawerStyle {
    val open: String
    val close: String
    val toolbar: String
    val drawerContent: String
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
        height = DRAWER_HEIGHT_CLOSE_XM_UP
    }
    "toolbar"(theme.mixins.toolbar.apply {
        backgroundColor = Color.transparent
    })
    "drawerContent" {
        marginTop = -DRAWER_HEIGHT_CLOSE_XM_UP

        (theme.breakpoints.up(Breakpoint.sm)) {
            marginTop = 0.px
        }
    }
    "expandIcon" {
        width = theme.spacing(3)
        margin(LinearDimension.auto, theme.spacing(2), LinearDimension.auto, LinearDimension.auto)
    }
}
