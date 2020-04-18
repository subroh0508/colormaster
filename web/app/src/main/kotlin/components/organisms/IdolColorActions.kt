package components.organisms

import components.atoms.supportableCheckBox
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.buttonGroup
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.div

fun RBuilder.idolColorActions(handler: RHandler<IdolColorActionsProps>) = child(IdolColorActionsComponent, handler = handler)

private val IdolColorActionsComponent = functionalComponent<IdolColorActionsProps> { props ->
    val classes = useStyle()

    div(classes.root) {
        buttonGroup {
            attrs.classes(classes.buttonGroup)

            button {
                attrs {
                    classes(classes.checkBox)
                    disableRipple = true
                    disableTouchRipple = true
                }

                supportableCheckBox {
                    attrs {
                        clearedAll = props.selected.isNotEmpty()
                        onClickCheckedAll = { console.log("checked all") }
                        onClickClearedAll = { console.log("cleared all") }
                    }
                }
            }
            button {
                attrs {
                    variant = ButtonVariant.outlined
                    onClickFunction = { props.onClickPreview() }
                }

                +"プレビュー"
            }
        }
    }
}

external interface IdolColorActionsProps : RProps {
    var selected: List<IdolColor>
    var onClickPreview: () -> Unit
}

external interface IdolColorActionsStyle {
    val root: String
    val buttonGroup: String
    val checkBox: String
}

private val useStyle = makeStyles<IdolColorActionsStyle> {
    "root" {
        flexGrow = 1.0
        display = Display.flex
        margin(8.px)
    }
    "buttonGroup" {
        marginLeft = LinearDimension.auto
    }
    "checkBox" {
        descendants("button") {
            padding(0.px)
        }
    }
}
