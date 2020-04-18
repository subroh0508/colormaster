package components.organisms

import components.molecules.supportableCheckBox
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.buttonGroup
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.div

fun RBuilder.idolColorGridsActions(handler: RHandler<IdolColorGridsActionsProps>) = child(IdolColorGridsActionsComponent, handler = handler)

private val IdolColorGridsActionsComponent = functionalComponent<IdolColorGridsActionsProps> { props ->
    val classes = useStyle()

    div(classes.root) {
        buttonGroup {
            attrs.classes(classes.buttonGroup)

            button {
                attrs {
                    variant = ButtonVariant.outlined
                    onClickFunction = { props.onClickPreview() }
                }

                +"プレビュー"
            }

            button {
                attrs {
                    classes(classes.checkBox)
                    disableRipple = true
                    disableTouchRipple = true
                }

                supportableCheckBox {
                    attrs {
                        clearedAll = props.selected.isNotEmpty()
                        onClickCheckedAll = { props.onClickSelectAll(true) }
                        onClickClearedAll = { props.onClickSelectAll(false) }
                    }
                }
            }
        }
    }
}

external interface IdolColorGridsActionsProps : RProps {
    var selected: List<IdolColor>
    var onClickPreview: () -> Unit
    var onClickSelectAll: (Boolean) -> Unit
}

external interface IdolColorGridsActionsStyle {
    val root: String
    val buttonGroup: String
    val checkBox: String
}

private val useStyle = makeStyles<IdolColorGridsActionsStyle> {
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
