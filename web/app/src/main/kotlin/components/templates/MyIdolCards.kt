package components.templates

import components.organisms.IDOL_COLOR_GRID_ACTIONS_CLASS_NAME
import components.organisms.idolColorGrids
import components.organisms.idolColorGridsActions
import kotlinx.css.*
import materialui.components.card.card
import materialui.components.cardcontent.cardContent
import materialui.components.cardheader.cardHeader
import materialui.components.container.container
import materialui.components.icon.icon
import materialui.components.typography.typographyH6
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.muitheme.spacing
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem
import react.*
import react.dom.div
import react.dom.span
import utilities.invoke
import utilities.useTranslation

fun RBuilder.myIdolsCards(handler: RHandler<MyIdolCardsProps>) = child(MyIdolCardsComponent, handler = handler)

external interface MyIdolCardsProps : RProps {
    var inCharges: List<IdolColorListItem>
    var favorites: List<IdolColorListItem>
    var onDoubleClickIdolColor: (IdolColor) -> Unit
    var onSelectInChargeOf: (item: IdolColor, isSelected: Boolean) -> Unit
    var onSelectInChargeOfAll: (isSelected: Boolean) -> Unit
    var onSelectFavorite: (item: IdolColor, isSelected: Boolean) -> Unit
    var onSelectFavoritesAll: (isSelected: Boolean) -> Unit
    var onClickPreview: (items: List<IdolColor>) -> Unit
    var onClickPenlight: (items: List<IdolColor>) -> Unit
}

private val MyIdolCardsComponent = functionComponent<MyIdolCardsProps> { props ->
    val classes = useStyles()

    val inCharges = props.inCharges.filter(IdolColorListItem::selected).map {
        IdolColor(it.id, it.name.value, it.hexColor)
    }
    val favorites = props.favorites.filter(IdolColorListItem::selected).map {
        IdolColor(it.id, it.name.value, it.hexColor)
    }

    container {
        div(classes.root) {
            inChargeOfIdolsCard {
                    idolColorGrids {
                    attrs {
                        items = props.inCharges
                        onClick = props.onSelectInChargeOf
                        onDoubleClick = props.onDoubleClickIdolColor
                    }
                }

                idolColorGridsActions {
                    attrs {
                        showLabel = false
                        selected = inCharges
                        onClickSelectAll = props.onSelectInChargeOfAll
                        onClickPreview = { props.onClickPreview(inCharges) }
                        onClickPenlight = { props.onClickPenlight(inCharges) }
                    }
                }
            }
            myFavoriteIdolsCard {
                idolColorGrids {
                    attrs {
                        items = props.favorites
                        onClick = props.onSelectFavorite
                        onDoubleClick = props.onDoubleClickIdolColor
                    }
                }

                idolColorGridsActions {
                    attrs {
                        showLabel = false
                        selected = favorites
                        onClickSelectAll = props.onSelectFavoritesAll
                        onClickPreview = { props.onClickPreview(favorites) }
                        onClickPenlight = { props.onClickPenlight(favorites) }
                    }
                }
            }
        }
    }
}

private fun RBuilder.inChargeOfIdolsCard(
    handler: RHandler<RProps>,
) = cardFrame("star", "myPage.myIdols.inCharges", handler)
private fun RBuilder.myFavoriteIdolsCard(
    handler: RHandler<RProps>,
) = cardFrame("favorite", "myPage.myIdols.favorites", handler)

private fun RBuilder.cardFrame(
    icon: String,
    labelKey: String,
    handler: RHandler<RProps>
) = child(functionComponent { props ->
    val classes = useStyles()
    val (t, _) = useTranslation()

    card {
        attrs.className = MY_IDOL_CARD_ELEMENT_CLASS_NAME
        attrs.classes(classes.card)
        attrs["variant"] = "outlined"

        cardHeader {
            attrs.title {
                span(classes.cardTitle) {
                    icon {
                        attrs.classes(classes.cardTitleIcon)
                        +"${icon}_icon"
                    }
                    typographyH6 { +t(labelKey) }
                }
            }
        }

        cardContent {
            attrs.classes(classes.cardContent)

            props.children()
        }
    }
}, handler = handler)

private const val MY_IDOL_CARD_ELEMENT_CLASS_NAME = "my-idol-card-element"

private external interface MyIdolCardsStyles {
    val root: String
    val card: String
    val cardTitle: String
    val cardTitleIcon: String
    val cardContent: String
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
            paddingRight = 0.px

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
    "cardTitle" {
        display = Display.flex
        flexDirection = FlexDirection.row
        alignItems = Align.center
    }
    "cardTitleIcon" {
        marginRight = 2.px
    }
    "cardContent" {
        paddingTop = 0.px
    }
}
