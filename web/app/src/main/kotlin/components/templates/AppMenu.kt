package components.templates

import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyDownFunction
import materialui.components.divider.divider
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import materialui.styles.muitheme.spacing
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.router.dom.useHistory

fun RBuilder.appMenu(handler: RHandler<AppMenuProps>) = child(AppMenuComponent, handler = handler)

private val AppMenuComponent = functionalComponent<AppMenuProps> { props ->
    val classes = useStyles()
    val history = useHistory()

    list {
        menus().forEach { (label, menus) ->
            list {
                attrs.classes(classes.parent)

                typography {
                    attrs.classes(classes.parentLabel)
                    attrs.variant = TypographyVariant.subtitle1
                    +label
                }

                menus.forEach { menu ->
                    listItem {
                        key = menu.id
                        attrs.classes(classes.nested)
                        attrs.onClickFunction = { history.push(menu.path) }
                        listItemText { attrs.primary { +menu.label } }
                    }
                }
            }
            divider {}
        }
    }
}

external interface AppMenuProps : RProps {
    var onCloseMenu: () -> Unit
}

private external interface AppMenuStyle {
    val parent: String
    val parentLabel: String
    val nested: String
}

private val useStyles = makeStyles<AppMenuStyle> {
    "parent" {
        paddingLeft = theme.spacing(2)
    }
    "parentLabel" {
        fontWeight = FontWeight.w700
    }
    "nested" {
        width = 100.pct
        paddingLeft = theme.spacing(4)
    }
}

external interface AppMenuItem {
    var id: String
    var path: String
    var label: String
}

fun menus() = arrayOf(
    "検索" to searchMenus(),
    "このサイトについて" to aboutMenus()
)

fun searchMenus(): Array<AppMenuItem> = arrayOf(
    jsObject { id = "search-idol"; path = "/"; label = "アイドル検索" },
    jsObject { id = "search-unit"; path = "/unit"; label = "ユニット検索" },
    jsObject { id = "search-music"; path = "/music"; label = "楽曲検索" }
)

fun aboutMenus(): Array<AppMenuItem> = arrayOf(
    jsObject { id = "about-howtouse"; path = "/about#houtouse"; label = "使い方" },
    jsObject { id = "about-license"; path = "/about#license"; label = "オープンソースライセンス" },
    jsObject { id = "about-github"; path = ""; label = "GitHub" }
)
