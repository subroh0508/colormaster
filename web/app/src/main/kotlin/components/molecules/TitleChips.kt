package components.molecules

import components.atoms.chip
import kotlinx.css.*
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.Titles
import react.*
import react.dom.div
import react.dom.p

fun RBuilder.titleChips(handler: RHandler<TitleChipsProps>) = child(TitleChipsComponent, handler = handler)

private val TitleChipsComponent = functionalComponent<TitleChipsProps> { props ->
    val classes = useStyles()

    div {
        p(classes.title) { +"ブランド" }

        div(classes.chips) {
            Titles.values().forEach { title ->
                val checked = props.title == title

                chip {
                    attrs {
                        label = title.displayName
                        isChecked = checked
                        onClick = { props.onSelect?.invoke(title, !checked) }
                    }
                }
            }
        }
    }
}

external interface TitleChipsProps : RProps {
    var title: Titles?
    var onSelect: ((Titles, Boolean) -> Unit)?
}

private external interface TitleChipsStyle {
    val title: String
    val chips: String
}

private val useStyles = makeStyles<TitleChipsStyle> {
    "title" {
        flexGrow = 1.0
    }
    "chips" {
        display = Display.flex
        flexWrap = FlexWrap.wrap
    }
}

