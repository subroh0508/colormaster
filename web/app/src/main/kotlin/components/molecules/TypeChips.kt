package components.molecules

import components.atoms.checkbox
import kotlinx.css.*
import materialui.components.formgroup.formGroup
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import materialui.styles.palette.primary
import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.Types
import react.*
import react.dom.div
import utilities.invoke
import utilities.useTranslation

fun RBuilder.typesChips(brands: Brands?, handler: RHandler<TypeChipsProps>) = when (brands) {
    Brands._765, Brands._ML -> typesMillionLiveChips(handler)
    Brands._CG -> typesCinderellaGirlsChips(handler)
    Brands._315 -> typesSideMChips(handler)
    else -> null
}

fun RBuilder.typesCinderellaGirlsChips(handler: RHandler<TypeChipsProps>) = child(TypeChipsComponent) { handler(); attrs.allTypes = Types.CINDERELLA_GIRLS.values().toSet() }
fun RBuilder.typesMillionLiveChips(handler: RHandler<TypeChipsProps>) = child(TypeChipsComponent) { handler(); attrs.allTypes = Types.MILLION_LIVE.values().toSet() }
fun RBuilder.typesSideMChips(handler: RHandler<TypeChipsProps>) = child(TypeChipsComponent) { handler(); attrs.allTypes = Types.SIDE_M.values().toSet() }

private val TypeChipsComponent = functionalComponent<TypeChipsProps> { props ->
    val classes = useStyles()
    val (t, _) = useTranslation()

    div {
        typography(p = true) {
            attrs {
                classes(classes.title)
                variant = TypographyVariant.subtitle1
            }

            +t("searchBox.attributes.type.label")
        }

        div(classes.checkboxes) {
            formGroup {
                attrs.row = true
                props.allTypes.forEach { type ->
                    val checked = props.types.contains(type)

                    checkbox {
                        attrs {
                            label = t(type.displayNameKey)
                            isChecked = checked
                            onClick = { props.onSelect?.invoke(type, !checked) }
                        }
                    }
                }
            }
        }
    }
}

private val Types.displayNameKey: String get() = when (this) {
    Types.CINDERELLA_GIRLS.CU -> "searchBox.attributes.type.cute"
    Types.CINDERELLA_GIRLS.CO -> "searchBox.attributes.type.cool"
    Types.CINDERELLA_GIRLS.PA -> "searchBox.attributes.type.passion"
    Types.MILLION_LIVE.PRINCESS -> "searchBox.attributes.type.princess"
    Types.MILLION_LIVE.FAIRY -> "searchBox.attributes.type.fairy"
    Types.MILLION_LIVE.ANGEL -> "searchBox.attributes.type.angel"
    Types.SIDE_M.PHYSICAL -> "searchBox.attributes.type.physical"
    Types.SIDE_M.INTELLIGENT -> "searchBox.attributes.type.intelligent"
    Types.SIDE_M.MENTAL -> "searchBox.attributes.type.mental"
    else -> ""
}

external interface TypeChipsProps : RProps {
    var types: Set<Types>
    var allTypes: Set<Types>
    var onSelect: ((Types, Boolean) -> Unit)?
}

private external interface TypeChipsStyle {
    val title: String
    val checkboxes: String
}

private val useStyles = makeStyles<TypeChipsStyle> {
    "title" {
        flexGrow = 1.0
        color = theme.palette.text.primary
    }
    "checkboxes" {
        display = Display.flex
        flexWrap = FlexWrap.wrap
    }
}
