package components.templates

import kotlinx.css.flexGrow
import kotlinx.css.marginRight
import kotlinx.css.zIndex
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
import materialui.styles.mixins.toolbar
import materialui.styles.muitheme.spacing
import react.*
import react.dom.div

fun RBuilder.appFrame(handler: RHandler<RProps>) = child(AppFrameComponent, handler = handler)

private val AppFrameComponent = functionalComponent<RProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        appBar {
            attrs {
                classes(classes.appBar)
                color = AppBarColor.default
                position = AppBarPosition.fixed
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

    div(classes.toolbar) {}
    props.children()
}

private external interface AppFrameStyle {
    val root: String
    val appBar: String
    val menuButton: String
    val title: String
    val toolbar: String
}

private val useStyles = makeStyles<AppFrameStyle> {
    "root" {
        flexGrow = 1.0
    }
    "appBar" {
        zIndex = theme.zIndex.drawer.toInt() + 1
    }
    "menuButton" {
        marginRight = theme.spacing(2)
    }
    "title" {
        flexGrow = 1.0
    }
    css["toolbar"] = theme.mixins.toolbar
}
