package components.templates

import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
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
        parent(classes, "検索")
        nestedListItem(
            classes,
            id = "search-idol", label = "アイドル検索"
        ) { history.push("/") }
        divider {}
        parent(classes, "このアプリについて")
        nestedListItem(
            classes,
            id = "about-howtouse", label = "使い方"
        ) { history.push("/howtouse") }
        nestedListItem(
            classes,
            id = "about-development", label = "仕組み"
        ) { history.push("/development") }
        nestedListItem(
            classes,
            id = "about-terms", label = "利用規約"
        ) { history.push("/terms") }
        divider {}
        anchorItem(
            classes,
            id = "github", label = "GitHub", href = "https://github.com/subroh0508/colormaster"
        )
        anchorItem(
            classes,
            id = "twitter", label = "開発者Twitter", href = "https://twitter.com/subroh_0508"
        )
    }
}

private fun RBuilder.parent(
    classes: AppMenuStyle,
    label: String
) = list {
    attrs.classes(classes.parent)

    typography {
        attrs.classes(classes.parentLabel)
        attrs.variant = TypographyVariant.subtitle1
        +label
    }
}

private fun RBuilder.nestedListItem(
    classes: AppMenuStyle,
    id: String,
    label: String,
    onClick: () -> Unit
) = list {
    key = id
    attrs.classes(classes.item)
    button {
        attrs.classes(classes.itemButton)
        attrs.onClickFunction = { onClick() }
        +label
    }
}

private fun RBuilder.anchorItem(
    classes: AppMenuStyle,
    id: String,
    label: String,
    href: String,
    icon: String = "launch_icon"
) = a {
    attrs.href = href
    attrs.target = "_blank"

    listItem {
        key = id
        listItemIcon {
            attrs.classes(classes.itemIcon)

            icon { +icon }
        }
        listItemText {
            attrs.primary {
                typography {
                    attrs.classes(classes.parentLabel)
                    attrs.variant = TypographyVariant.subtitle1
                    +label
                }
            }
        }
    }
}

external interface AppMenuProps : RProps {
    var onCloseMenu: () -> Unit
}

private external interface AppMenuStyle {
    val title: String
    val parent: String
    val parentLabel: String
    val item: String
    val itemButton: String
    val itemIcon: String
}

private val useStyles = makeStyles<AppMenuStyle> {
    "title" {
        marginBottom = theme.spacing(2)
    }
    "parent" {
        display = Display.flex
        paddingLeft = theme.spacing(2)
    }
    "parentLabel" {
        fontWeight = FontWeight.w700
    }
    "item" {
        display = Display.flex
        paddingTop = 0.px
        paddingBottom = 0.px
    }
    "itemButton" {
        width = 100.pct
        paddingLeft = theme.spacing(4)
        justifyContent = JustifyContent.flexStart
    }
    "itemIcon" {
        minWidth = 36.px
    }
}
