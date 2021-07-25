package pages

import components.templates.myIdolsCards
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import react.*
import react.dom.div

@Suppress("FunctionName")
fun RBuilder.MyIdolsPage() = child(MyIdolsPageComponent)

private val MyIdolsPageComponent = functionalComponent<RProps> {
    val classes = useStyles()

    div {
        div(classes.toolbar) {}
        myIdolsCards()
    }
}

private external interface MyIdolsPageStyle {
    val toolbar: String
}

private val useStyles = makeStyles<MyIdolsPageStyle> {
    "toolbar"(theme.mixins.toolbar)
}
