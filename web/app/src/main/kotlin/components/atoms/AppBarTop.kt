package components.atoms

import components.templates.APP_BAR_SM_UP
import kotlinx.css.*
import kotlinx.css.properties.BoxShadows
import kotlinx.html.js.onClickFunction
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarColor
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.button.enums.ButtonColor
import materialui.components.icon.icon
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.iconButton
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.muitheme.spacing
import materialui.styles.palette.PaletteType
import materialui.styles.palette.paper
import org.w3c.dom.events.Event
import react.*
import react.dom.div

fun RBuilder.appBarTop(handler: RHandler<AppBarTopProps>) = child(AppBarTopComponent, handler = handler)

private val AppBarTopComponent = functionalComponent<AppBarTopProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        appBar {
            attrs {
                classes(classes.appBar)
                color = AppBarColor.default
                position = AppBarPosition.fixed
            }

            toolbar {
                iconButton {
                    attrs {
                        classes(classes.menuButton)
                        color = ButtonColor.inherit
                        edge = IconButtonEdge.start
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

                iconButton {
                    attrs {
                        color = ButtonColor.inherit
                        edge = IconButtonEdge.end
                        onClickFunction = props.onClickChangeTheme
                    }

                    icon { +"brightness_${if (props.themeType == PaletteType.light) "4" else "7" }_icon" }
                }
            }
        }
    }
}

external interface AppBarTopProps : RProps {
    var themeType: PaletteType
    var onClickChangeTheme: (event: Event) -> Unit
}

private external interface AppBarTopStyle {
    val root: String
    val appBar: String
    var menuButton: String
    var title: String
}

private val useStyles = makeStyles<AppBarTopStyle> {
    "root" {
        flexGrow = 1.0
    }
    "appBar" {
        zIndex = theme.zIndex.drawer.toInt() + 1
        backgroundColor = theme.palette.background.paper
        boxShadow = BoxShadows.none

        (theme.breakpoints.up(Breakpoint.sm)) {
            left = 0.px
            width = APP_BAR_SM_UP
        }
    }
    "menuButton" {
        marginRight = theme.spacing(2)
    }
    "title" {
        flexGrow = 1.0
    }
}
