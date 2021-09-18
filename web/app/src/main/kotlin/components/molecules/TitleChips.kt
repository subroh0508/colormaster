package components.molecules

import components.atoms.chip
import kotlinx.css.*
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import materialui.styles.palette.primary
import net.subroh0508.colormaster.model.Brands
import react.*
import react.dom.attrs
import react.dom.div
import utilities.invoke
import utilities.useTranslation

fun RBuilder.titleChips(handler: RHandler<TitleChipsProps>) = child(TitleChipsComponent, handler = handler)

private val TitleChipsComponent = functionComponent<TitleChipsProps> { props ->
    val classes = useStyles()
    val (t, _) = useTranslation()

    div {
        typography(p = true) {
            attrs {
                classes(classes.title)
                variant = TypographyVariant.subtitle1
            }

            +t("searchBox.attributes.brands")
        }

        div(classes.chips) {
            Brands.values().forEach { title ->
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
    var title: Brands?
    var onSelect: ((Brands, Boolean) -> Unit)?
}

private external interface TitleChipsStyle {
    val title: String
    val chips: String
}

private val useStyles = makeStyles<TitleChipsStyle> {
    "title" {
        flexGrow = 1.0
        color = theme.palette.text.primary
    }
    "chips" {
        display = Display.flex
        flexWrap = FlexWrap.wrap
    }
}

