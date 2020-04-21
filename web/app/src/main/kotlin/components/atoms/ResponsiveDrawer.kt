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
    val (opened, openDrawer) = useState(false)

    val rootStyle = if (opened) classes.open else classes.close

    hidden {
        attrs { smUp = true }

        drawer(
            DrawerStyle.root to rootStyle,
            DrawerStyle.paper to rootStyle
        ) {
            attrs {
                variant = DrawerVariant.permanent
                anchor = props.anchor
                open = opened
                onClose = { openDrawer(false) }
            }

            if (opened) {
                div(classes.toolbar) {}
            }

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

            props.children()
        }
    }
}

external interface ResponsiveDrawerProps : RProps {
    var anchor: DrawerAnchor
    var opened: Boolean
    var onClose: () -> Unit
}

private external interface ResponsiveDrawerStyle {
    val open: String
    val close: String
    val toolbar: String
    val expandIcon: String
}

private val useStyles = makeStyles<ResponsiveDrawerStyle> {
    "open" {
        (theme.breakpoints.up(Breakpoint.sm)) {
            width = DRAWER_WIDTH_SM_UP
        }
    }
    "close" {
        height = DRAWER_HEIGHT_CLOSE_XM_UP
    }
    "toolbar"(theme.mixins.toolbar)
    "expandIcon" {
        width = theme.spacing(3)
        margin(theme.spacing(1), LinearDimension.auto, theme.spacing(1), 12.px)
    }
}
