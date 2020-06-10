package components.atoms

import basename
import kotlinx.css.*
import kotlinx.css.properties.BoxShadows
import kotlinx.html.A
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyDownFunction
import kotlinx.html.lang
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarColor
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.icon.enums.IconFontSize
import materialui.components.icon.icon
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.iconButton
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.toolbar.toolbar
import materialui.components.tooltip.tooltip
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.muitheme.spacing
import materialui.styles.palette.PaletteType
import materialui.styles.palette.default
import net.subroh0508.colormaster.model.Languages
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import utilities.invoke
import utilities.useTranslation

val APP_BAR_SM_UP = 408.px

fun RBuilder.appBarTop(handler: RHandler<AppBarTopProps>) = child(AppBarTopComponent, handler = handler)

private val AppBarTopComponent = functionalComponent<AppBarTopProps> { props ->
    val classes = useStyles(props)
    val appBarStyle = "${classes.appBar} ${if (props.expand) classes.appBarExpand else ""}"
    val (t, _) = useTranslation()

    div(classes.root) {
        appBar {
            attrs {
                classes(appBarStyle)
                color = AppBarColor.default
                position = AppBarPosition.fixed
            }

            toolbar {
                iconButton {
                    attrs {
                        classes(classes.menuButton)
                        color = ButtonColor.inherit
                        edge = IconButtonEdge.start
                        onClickFunction = props.onClickMenuIcon
                    }

                    icon { +"menu_icon" }
                }

                typography {
                    attrs {
                        classes(classes.title)
                        variant = TypographyVariant.h6
                    }
                }

                child(LanguageMenuComponent) {
                    attrs {
                        lang = props.lang
                        pathname = props.pathname
                    }
                }

                tooltip {
                    attrs { title { +t("appBar.${if (props.themeType == PaletteType.light) "darkTheme" else "lightTheme"}") } }

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

    drawer {
        attrs {
            anchor = DrawerAnchor.left
            open = props.openDrawer
            onClose = { props.onCloseMenu() }
        }

        div(classes.list) {
            attrs.onClickFunction = { props.onCloseMenu() }
            attrs.onKeyDownFunction = { props.onCloseMenu() }
            props.MenuComponent?.let { child(it) }
        }
    }
}

external interface AppBarTopProps : RProps {
    var themeType: PaletteType
    var lang: Languages
    var pathname: String
    var openDrawer: Boolean
    var expand: Boolean
    @Suppress("PropertyName")
    var MenuComponent: ReactElement?
    var onClickChangeTheme: (event: Event) -> Unit
    var onClickMenuIcon: (event: Event) -> Unit
    var onCloseMenu: () -> Unit
}

@Suppress("FunctionName")
fun AppBarTopProps.MenuComponent(block: RBuilder.() -> Unit) {
    MenuComponent = buildElement(block)
}

private val LanguageMenuComponent = functionalComponent<LanguageMenuProps> { props ->
    val (languageMenu, setLanguageMenu) = useState<HTMLButtonElement?>(null)
    val (t, _) = useTranslation()

    val path = props.pathname.replace(props.lang.basename, "")

    fun handleLanguageIconClick(event: Event) = setLanguageMenu(event.currentTarget as HTMLButtonElement)
    fun handleLanguageMenuClose(event: Event) = setLanguageMenu(null)

    tooltip {
        attrs { title { +t("appBar.changeLanguage") } }

        button {
            attrs {
                color = ButtonColor.inherit
                onClickFunction = ::handleLanguageIconClick
                setProp("aria-owns", languageMenu?.let { "language-menu" } ?: undefined)
                setProp("aria-haspopup", true)
            }

            icon { +"translate_icon" }
            icon { attrs.fontSize = IconFontSize.small; +"expand_more_icon" }
        }
    }

    menu {
        attrs {
            id = "language-menu"
            setProp("anchorEl", languageMenu)
            open = languageMenu != null
            onClose = { e, _ -> handleLanguageMenuClose(e) }
        }

        Languages.values().forEach { language ->
            menuItem(factory = { A(mapOf(), it) }) {
                attrs {
                    key = language.code
                    href = "${language.basename}$path"
                    hrefLang = language.code
                    selected = props.lang.code == language.code
                    lang = language.code
                    onClickFunction = ::handleLanguageMenuClose
                }

                +language.label
            }
        }
    }
}

private external interface LanguageMenuProps : RProps {
    var lang: Languages
    var pathname: String
}

private external interface AppBarTopStyle {
    val root: String
    val appBar: String
    val appBarExpand: String
    val menuButton: String
    val title: String
    val list: String
}

private val useStyles = makeStyles<AppBarTopStyle, AppBarTopProps> {
    "root" {
        flexGrow = 1.0
    }
    "appBar" {
        zIndex = theme.zIndex.drawer.toInt() + 1
        backgroundColor = theme.palette.background.default
        boxShadow = BoxShadows.none

        (theme.breakpoints.up(Breakpoint.sm)) {
            left = 0.px
            width = APP_BAR_SM_UP
        }
    }
    "appBarExpand" {
        (theme.breakpoints.up(Breakpoint.sm)) {
            width = 100.pct
        }
    }
    "menuButton" {
        marginRight = theme.spacing(2)
    }
    "title" {
        flexGrow = 1.0
    }
    "list" {
        width = 270.px
        height = 100.pct
    }
}
