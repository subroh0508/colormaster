package pages

import components.templates.myIdolsCards
import containers.MyIdolsProviderContext
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import react.*
import react.dom.div

@Suppress("FunctionName")
fun RBuilder.MyIdolsPage() = child(MyIdolsPageComponent)

private val MyIdolsPageComponent = functionalComponent<RProps> {
    val classes = useStyles()

    val myIdolsUiModel = useContext(MyIdolsProviderContext)

    div {
        div(classes.toolbar) {}
        myIdolsCards {
            attrs.favorites = myIdolsUiModel.favorites
            attrs.managed = myIdolsUiModel.managed
        }
    }
}

private external interface MyIdolsPageStyle {
    val toolbar: String
}

private val useStyles = makeStyles<MyIdolsPageStyle> {
    "toolbar"(theme.mixins.toolbar)
}
