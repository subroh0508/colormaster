package components.templates

import components.organisms.idolColorGrids
import kotlinx.css.flexGrow
import kotlinx.css.marginBottom
import kotlinx.css.paddingTop
import kotlinx.css.px
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.cardheader.cardHeader
import materialui.components.container.container
import materialui.components.typography.typographyH6
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.muitheme.spacing
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem
import react.*
import react.dom.div
import utilities.invoke
import utilities.useTranslation

fun RBuilder.myIdolsCards(handler: RHandler<MyIdolCardsProps>) = child(MyIdolCardsComponent, handler = handler)

external interface MyIdolCardsProps : RProps {
    var managed: List<IdolColorListItem>
    var favorites: List<IdolColorListItem>
    var onSelectManaged: (item: IdolColor, isSelected: Boolean) -> Unit
    var onSelectFavorite: (item: IdolColor, isSelected: Boolean) -> Unit
}

private val MyIdolCardsComponent = functionalComponent<MyIdolCardsProps> { props ->
    val classes = useStyles()

    container {
        div(classes.root) {
            managedIdolsCard {
                idolColorGrids {
                    attrs {
                        items = props.managed
                        onClick = props.onSelectManaged
                    }
                }
            }
            myFavoriteIdolsCard {
                idolColorGrids {
                    attrs {
                        items = props.favorites
                        onClick = props.onSelectFavorite
                    }
                }
            }
        }
    }
}

private fun RBuilder.managedIdolsCard(
    handler: RHandler<RProps>,
) = cardFrame("myPage.myIdols.managed", handler)
private fun RBuilder.myFavoriteIdolsCard(
    handler: RHandler<RProps>,
) = cardFrame("myPage.myIdols.favorites", handler)

private fun RBuilder.cardFrame(
    labelKey: String,
    handler: RHandler<RProps>
) = child(functionalComponent { props ->
    val classes = useStyles()
    val (t, _) = useTranslation()

    card {
        attrs.className = MY_IDOL_CARD_ELEMENT_CLASS_NAME
        attrs.classes(classes.card)
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                typographyH6 { +t(labelKey) }
            }
        }

        cardContent { props.children() }
    }
}, handler = handler)

private const val MY_IDOL_CARD_ELEMENT_CLASS_NAME = "my-idol-card-element"

private external interface MyIdolCardsStyles {
    val root: String
    val card: String
}

private val useStyles = makeStyles<MyIdolCardsStyles> {
    "root" {
        paddingTop = theme.spacing(2)

        descendants(".$MY_IDOL_CARD_ELEMENT_CLASS_NAME") {
            marginBottom = theme.spacing(4)
        }
    }
    "card" {
        flexGrow = 1.0
        marginBottom = 60.px

        (theme.breakpoints.up(Breakpoint.sm)) {
            marginBottom = 0.px
        }
    }
}
