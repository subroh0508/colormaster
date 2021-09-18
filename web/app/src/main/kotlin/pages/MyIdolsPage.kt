package pages

import components.templates.myIdolsCards
import containers.MyIdolsDispatcherContext
import containers.MyIdolsProviderContext
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem
import react.*
import react.dom.div
import react.router.dom.useHistory
import toPenlight
import toPreview

@Suppress("FunctionName")
fun RBuilder.MyIdolsPage() = child(MyIdolsPageComponent)

private val MyIdolsPageComponent = functionComponent<RProps> {
    val classes = useStyles()
    val history = useHistory()

    val myIdolsViewModel = useContext(MyIdolsDispatcherContext)
    val myIdolsUiModel = useContext(MyIdolsProviderContext)

    val inCharges = myIdolsUiModel.inCharges.map {
        IdolColorListItem(it, selected = myIdolsUiModel.selectedInCharges.contains(it.id))
    }
    val favorites = myIdolsUiModel.favorites.map {
        IdolColorListItem(it, selected = myIdolsUiModel.selectedFavorites.contains(it.id))
    }

    fun query(items: List<IdolColor>) = items.joinToString("&") { "id=${it.id}" }

    div {
        div(classes.toolbar) {}
        myIdolsCards {
            attrs.inCharges = inCharges
            attrs.favorites = favorites
            attrs.onSelectInChargeOf = myIdolsViewModel::selectInChargeOf
            attrs.onSelectInChargeOfAll = myIdolsViewModel::selectInChargeOfAll
            attrs.onSelectFavorite = myIdolsViewModel::selectFavorite
            attrs.onSelectFavoritesAll = myIdolsViewModel::selectFavoritesAll
            attrs.onDoubleClickIdolColor = { history.toPenlight(query(listOf(it))) }
            attrs.onClickPreview = { history.toPreview(query(it)) }
            attrs.onClickPenlight = { history.toPenlight(query(it)) }
        }
    }
}

private external interface MyIdolsPageStyle {
    val toolbar: String
}

private val useStyles = makeStyles<MyIdolsPageStyle> {
    "toolbar"(theme.mixins.toolbar)
}
