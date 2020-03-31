package components.templates

import kotlinx.css.flexGrow
import kotlinx.css.marginRight
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarColor
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.button.enums.ButtonColor
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import materialui.styles.muitheme.spacing
import react.RProps
import react.dom.div
import react.functionalComponent

external interface AppFrameStyle {
    val root: String
    val menuButton: String
    val title: String
}

private val useStyles = makeStyles<AppFrameStyle> {
    "root" {
        flexGrow = 1.0
    }
    "menuButton" {
        marginRight = theme.spacing(2)
    }
    "title" {
        flexGrow = 1.0
    }
}

fun appFrame() = functionalComponent<RProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        appBar {
            attrs {
                color = AppBarColor.default
                position = AppBarPosition.static
            }

            toolbar {
                attrs {

                }

                iconButton {
                    attrs {
                        classes(classes.menuButton)
                        color = ButtonColor.inherit
                    }

                    icon { +"menu_icon" }
                }

                typography {
                    attrs {
                        classes(classes.title)
                        variant = TypographyVariant.h6
                    }

                    +"COLOR M@STER"
                }
            }
        }
    }

    props.children()
}
