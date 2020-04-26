package components.molecules

import components.atoms.checkbox
import kotlinx.css.*
import materialui.components.formgroup.formGroup
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import materialui.styles.palette.primary
import net.subroh0508.colormaster.model.Titles
import net.subroh0508.colormaster.model.Types
import react.*
import react.dom.div

fun RBuilder.typesChips(titles: Titles?, handler: RHandler<TypeChipsProps>) = when (titles) {
    Titles._765, Titles._ML -> typesMillionLiveChips(handler)
    Titles._CG -> typesCinderellaGirlsChips(handler)
    Titles._315 -> typesSideMChips(handler)
    else -> null
}

fun RBuilder.typesCinderellaGirlsChips(handler: RHandler<TypeChipsProps>) = child(TypeChipsComponent) { handler(); attrs.allTypes = Types.CINDERELLA_GIRLS.values().toSet() }
fun RBuilder.typesMillionLiveChips(handler: RHandler<TypeChipsProps>) = child(TypeChipsComponent) { handler(); attrs.allTypes = Types.MILLION_LIVE.values().toSet() }
fun RBuilder.typesSideMChips(handler: RHandler<TypeChipsProps>) = child(TypeChipsComponent) { handler(); attrs.allTypes = Types.SIDE_M.values().toSet() }

private val TypeChipsComponent = functionalComponent<TypeChipsProps> { props ->
    val classes = useStyles()

    div {
        typography(p = true) {
            attrs {
                classes(classes.title)
                variant = TypographyVariant.subtitle1
            }

            +"属性"
        }

        div(classes.checkboxes) {
            formGroup {
                attrs.row = true
                props.allTypes.forEach { type ->
                    val checked = props.types.contains(type)

                    checkbox {
                        attrs {
                            label = type.displayName
                            isChecked = checked
                            onClick = { props.onSelect?.invoke(type, !checked) }
                        }
                    }
                }
            }
        }
    }
}

private val Types.displayName: String get() = when (this) {
    Types.CINDERELLA_GIRLS.CU -> "Cute"
    Types.CINDERELLA_GIRLS.CO -> "Cool"
    Types.CINDERELLA_GIRLS.PA -> "Passion"
    Types.MILLION_LIVE.PRINCESS -> "Princess"
    Types.MILLION_LIVE.FAIRY -> "Fairy"
    Types.MILLION_LIVE.ANGEL -> "Angel"
    Types.SIDE_M.PHYSICAL -> "フィジカル"
    Types.SIDE_M.INTELLIGENT -> "インテリ"
    Types.SIDE_M.MENTAL -> "メンタル"
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
