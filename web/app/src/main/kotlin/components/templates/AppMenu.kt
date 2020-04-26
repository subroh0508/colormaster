package components.templates

import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.label
import materialui.components.divider.divider
import materialui.components.icon.icon
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.components.typography.typographyH6
import materialui.styles.makeStyles
import materialui.styles.muitheme.spacing
import react.*
import react.dom.a
import react.router.dom.useHistory

fun RBuilder.appMenu(handler: RHandler<AppMenuProps>) = child(AppMenuComponent, handler = handler)

private val AppMenuComponent = functionalComponent<AppMenuProps> { props ->
    val classes = useStyles()
    val history = useHistory()

    list {
        listItem {
            attrs.classes(classes.title)
            listItemText {
                attrs.primary {
                    typographyH6 {
                        +"COLOR M@STER"
                    }
                }
                attrs.secondary {
                    +"v2020.04.20-beta"
                }
            }
        }
        divider {}
        parent(classes, "検索") {
            nestedListItem(
                classes.nested,
                id = "search-idol", label = "アイドル検索"
            ) { history.push("/") }
            nestedListItem(
                classes.nested,
                id = "search-unit", label = "ユニット検索"
            ) { history.push("/unit") }
            nestedListItem(
                classes.nested,
                id = "search-music", label = "楽曲検索"
            ) { history.push("/music") }
        }
        divider {}
        parent(classes, "このサイトについて") {
            nestedListItem(
                classes.nested,
                id = "about-howtouse", label = "使い方"
            ) { history.push("/howtouse") }
            nestedListItem(
                classes.nested,
                id = "about-licences", label = "オープンソースライセンス"
            ) { history.push("/licences") }
        }
        divider {}
        a {
            attrs.href = "https://github.com/subroh0508/colormaster"
            attrs.target = "_blank"

            listItem {
                key = "github"
                attrs.onClickFunction = { console.log("github") }
                listItemIcon {
                    icon { +"launch_icon" }
                }
                listItemText {
                    attrs.primary {
                        typography {
                            attrs.classes(classes.parentLabel)
                            attrs.variant = TypographyVariant.subtitle1
                            +"GitHub"
                        }
                    }
                }
            }
        }
    }
}

private fun RBuilder.parent(
    classes: AppMenuStyle,
    label: String,
    children: RBuilder.() -> Unit
) = list {
    attrs.classes(classes.parent)

    typography {
        attrs.classes(classes.parentLabel)
        attrs.variant = TypographyVariant.subtitle1
        +label
    }

    child(buildElements(children))
}

private fun RBuilder.nestedListItem(
    classes: String,
    id: String,
    label: String,
    onClick: () -> Unit
) = listItem {
    key = id
    attrs.classes(classes)
    attrs.onClickFunction = { onClick() }
    listItemText { attrs.primary { +label } }
}

external interface AppMenuProps : RProps {
    var onCloseMenu: () -> Unit
}

private external interface AppMenuStyle {
    val title: String
    val parent: String
    val parentLabel: String
    val nested: String
}

private val useStyles = makeStyles<AppMenuStyle> {
    "title" {
        marginBottom = theme.spacing(2)
    }
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
