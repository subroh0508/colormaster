package components.templates

import components.atoms.colorItem
import controllers.SearchController
import kotlinext.js.js
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.html.DIV
import kotlinx.html.style
import materialui.components.grid.grid
import materialui.components.paper.PaperProps
import materialui.components.paper.paper
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import react.*
import react.dom.br
import react.dom.div

external interface ColorGridsStyle {
    val root: String
    val container: String
    val paper: String
}

private val useStyles = makeStyles<ColorGridsStyle> {
    "root" {
        margin(8.px)
        paddingTop = 16.px
    }
    "container" {
        flexGrow = 1.0
        display = Display.flex
        flexDirection = FlexDirection.row
        flexWrap = FlexWrap.wrap
    }
    "paper" {
        width = 200.px
        height = 50.px
        margin(4.px)
        textAlign = TextAlign.center
        fontWeight = FontWeight.w700
    }
}

fun colorGrids() = functionalComponent<RProps> {
    val classes = useStyles()

    val (items, setItems) = useState(listOf<IdolColor>())

    useEffect(listOf()) {
        SearchController.loadItems(IdolName(""))
            .then { setItems(it) }
            .catch { console.log(it) }
    }

    div(classes.root) {
        div(classes.container) {
            items.forEach { idolColor ->
                colorItem {
                    name = idolColor.name
                    color = idolColor.color
                    isBrighter = idolColor.isBrighter
                }
            }
        }
    }
}
