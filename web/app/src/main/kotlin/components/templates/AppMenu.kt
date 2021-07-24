package components.templates

import AuthenticationDispatcherContext
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import components.atoms.link
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
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.home.viewmodel.JsAuthenticationViewModel
import react.*
import react.router.dom.useHistory
import toDevelopment
import toHowToUse
import toRoot
import toTerms
import utilities.I18nextText
import utilities.invoke
import utilities.useTranslation

fun RBuilder.appMenu(handler: RHandler<AppMenuProps>) = child(AppMenuComponent, handler = handler)

private val AppMenuComponent = functionalComponent<AppMenuProps> { props ->
    val classes = useStyles()
    val history = useHistory()
    val (t, _) = useTranslation()

    val viewModel = useContext(AuthenticationDispatcherContext)

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
                    +"v2021.07.18"
                }
            }
        }
        divider {}
        parent(classes, t("appMenu.search.label"))
        nestedListItem(
            classes,
            id = "search-idol", label = t("appMenu.search.attributes")
        ) { history.toRoot() }
        divider {}
        parent(classes, t("appMenu.about.label"))
        nestedListItem(
            classes,
            id = "about-howtouse", label = t("appMenu.about.howToUse")
        ) { history.toHowToUse() }
        nestedListItem(
            classes,
            id = "about-development", label = t("appMenu.about.development")
        ) { history.toDevelopment() }
        nestedListItem(
            classes,
            id = "about-terms", label = t("appMenu.about.terms")
        ) { history.toTerms() }
        divider {}
        parent(classes, t("appMenu.account.label"))
        signInWithGoogle(
            classes,
            t = t,
            currentUser = props.currentUser,
            viewModel = viewModel,
        )
        divider {}
        anchorItem(
            classes,
            id = "github", label = t("appMenu.links.github"), href = "https://github.com/subroh0508/colormaster"
        )
        anchorItem(
            classes,
            id = "twitter", label = t("appMenu.links.twitter"), href = "https://twitter.com/subroh_0508"
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

private fun RBuilder.signInWithGoogle(
    classes: AppMenuStyle,
    t: I18nextText,
    currentUser: CurrentUser?,
    viewModel: JsAuthenticationViewModel,
) = list {
    key = "sign-in-with-google"
    attrs.classes(classes.item)

    if (currentUser?.isAnonymous != false) {
        button {
            attrs.classes(classes.googleButton)
            attrs.onClickFunction = { viewModel.signInGoogle() }
        }
        return@list
    }

    button {
        attrs.classes(classes.itemButton)
        attrs.onClickFunction = { viewModel.signOut() }
        +t("appMenu.account.sign_out")
    }
}

private fun RBuilder.anchorItem(
    classes: AppMenuStyle,
    id: String,
    label: String,
    href: String,
    icon: String = "launch_icon"
) = link(href) {
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
    var currentUser: CurrentUser?
    var onCloseMenu: () -> Unit
}

private external interface AppMenuStyle {
    val title: String
    val parent: String
    val parentLabel: String
    val item: String
    val itemButton: String
    val googleButton: String
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
        textTransform = TextTransform.none
    }
    "googleButton" {
        height = 52.px
        width = 100.pct
        padding(0.px, theme.spacing(2))
        margin(theme.spacing(1), theme.spacing(4), theme.spacing(2))
        background = "url(/sign_in_with_google.png)"
        backgroundSize = "contain"
    }
    "itemIcon" {
        minWidth = 36.px
    }
}
