package components.organisms

import components.molecules.supportableCheckBox
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonSize
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.buttonGroup
import materialui.components.icon.icon
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.div

fun RBuilder.idolColorGridsActions(handler: RHandler<IdolColorGridsActionsProps>) = child(IdolColorGridsActionsComponent, handler = handler)

private val IdolColorGridsActionsComponent = functionalComponent<IdolColorGridsActionsProps> { props ->
    val classes = useStyle()

    buttonGroup {
        attrs.classes(classes.root)

        button {
            attrs {
                variant = ButtonVariant.outlined
                size = ButtonSize.small
                disabled = props.selected.isEmpty()
                onClickFunction = { props.onClickPreview() }
                startIcon { icon { +"palette_icon" } }
            }

            +"プレビュー"
        }

        button {
            attrs {
                variant = ButtonVariant.outlined
                size = ButtonSize.small
                disabled = props.selected.isEmpty()
                onClickFunction = { props.onClickPreview() }
                startIcon { icon { +"highlight_icon" } }
            }

            +"ペンライト"
        }

        val checkAll = props.selected.isEmpty()
        button {
            attrs {
                variant = ButtonVariant.outlined
                size = ButtonSize.small
                onClickFunction = { props.onClickSelectAll(checkAll) }
                startIcon { icon {
                    + if(checkAll) "check_box_outline_blank_icon" else "indeterminate_check_box_icon"
                } }
            }

            + if (checkAll) "すべて" else "選択解除"
        }

        /*
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
        */
    }
}

external interface IdolColorGridsActionsProps : RProps {
    var selected: List<IdolColor>
    var onClickPreview: () -> Unit
    var onClickSelectAll: (Boolean) -> Unit
}

external interface IdolColorGridsActionsStyle {
    val root: String
}

private val useStyle = makeStyles<IdolColorGridsActionsStyle> {
    "root" {
        width = 100.pct
        padding(8.px)

        descendants("button") {
            width = 100.pct / 3
        }
    }
}
