package components.molecules

import components.atoms.chip
import kotlinx.css.Display
import kotlinx.css.FlexWrap
import kotlinx.css.display
import kotlinx.css.flexWrap
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.Titles
import react.*
import react.dom.div

fun RBuilder.titleChips(handler: RHandler<TitleChipsProps>) = child(TitleChipsComponent, handler = handler)

private val TitleChipsComponent = functionalComponent<TitleChipsProps> { props ->
    val classes = useStyles()

    div(classes.root) {
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

external interface TitleChipsProps : RProps {
    var title: Titles?
    var onSelect: ((Titles, Boolean) -> Unit)?
}

private external interface TitleChipsStyle {
    val root: String
}

private val useStyles = makeStyles<TitleChipsStyle> {
    "root" {
        display = Display.flex
        flexWrap = FlexWrap.wrap
    }
}

