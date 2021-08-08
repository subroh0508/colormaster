package pages

import components.templates.myIdolsCards
import containers.MyIdolsDispatcherContext
import containers.MyIdolsProviderContext
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem
import react.*
import react.dom.div

@Suppress("FunctionName")
fun RBuilder.MyIdolsPage() = child(MyIdolsPageComponent)

private val MyIdolsPageComponent = functionalComponent<RProps> {
    val classes = useStyles()

    val myIdolsViewModel = useContext(MyIdolsDispatcherContext)
    val myIdolsUiModel = useContext(MyIdolsProviderContext)

    val inCharges = myIdolsUiModel.inCharges.map {
        IdolColorListItem(it, selected = myIdolsUiModel.selectedInCharges.contains(it.id))
    }
    val favorites = myIdolsUiModel.favorites.map {
        IdolColorListItem(it, selected = myIdolsUiModel.selectedFavorites.contains(it.id))
    }

    div {
        div(classes.toolbar) {}
        myIdolsCards {
            attrs.inCharges = inCharges
            attrs.favorites = favorites
            attrs.onSelectInChargeOf = myIdolsViewModel::selectInChargeOf
            attrs.onSelectFavorite = myIdolsViewModel::selectFavorite
        }
    }
}

private external interface MyIdolsPageStyle {
    val toolbar: String
}

private val useStyles = makeStyles<MyIdolsPageStyle> {
    "toolbar"(theme.mixins.toolbar)
}
