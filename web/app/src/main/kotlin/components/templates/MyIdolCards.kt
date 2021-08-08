package components.templates

import components.organisms.IDOL_COLOR_GRID_ACTIONS_CLASS_NAME
import components.organisms.idolColorGrids
import components.organisms.idolColorGridsActions
import kotlinx.css.*
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
    var inCharges: List<IdolColorListItem>
    var favorites: List<IdolColorListItem>
    var onSelectInChargeOf: (item: IdolColor, isSelected: Boolean) -> Unit
    var onSelectFavorite: (item: IdolColor, isSelected: Boolean) -> Unit
}

private val MyIdolCardsComponent = functionalComponent<MyIdolCardsProps> { props ->
    val classes = useStyles()

    container {
        div(classes.root) {
            inChargeOfIdolsCard {
                idolColorGrids {
                    attrs {
                        items = props.inCharges
                        onClick = props.onSelectInChargeOf
                    }
                }

                idolColorGridsActions {
                    attrs {
                        showLabel = false
                        selected = props.inCharges.filter(IdolColorListItem::selected).map { IdolColor(it.id, it.name.value, it.hexColor) }
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

                idolColorGridsActions {
                    attrs {
                        showLabel = false
                        selected = props.favorites.filter(IdolColorListItem::selected).map { IdolColor(it.id, it.name.value, it.hexColor) }
                    }
                }
            }
        }
    }
}

private fun RBuilder.inChargeOfIdolsCard(
    handler: RHandler<RProps>,
) = cardFrame("myPage.myIdols.inCharges", handler)
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

        descendants(".$IDOL_COLOR_GRID_ACTIONS_CLASS_NAME") {
            width = LinearDimension.auto
            float = Float.right

            descendants("button") {
                borderStyle = BorderStyle.none

                not(":last-of-type") {
                    width = 36.px

                    descendants("span") {
                        margin(0.px)
                    }
                }

                lastOfType {
                    width = 96.px
                    justifyContent = JustifyContent.left
                }
            }
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
