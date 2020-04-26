package components.organisms

import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonSize
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.buttonGroup
import materialui.components.icon.icon
import materialui.styles.makeStyles
import materialui.useMediaQuery
import net.subroh0508.colormaster.model.IdolColor
import react.*

const val IDOL_COLOR_GRID_ACTIONS_CLASS_NAME = "idol-color-grid-actions"

fun RBuilder.idolColorGridsActions(handler: RHandler<IdolColorGridsActionsProps>) = child(IdolColorGridsActionsComponent, handler = handler)

private val IdolColorGridsActionsComponent = functionalComponent<IdolColorGridsActionsProps> { props ->
    val classes = useStyle()
    val showLabel = useMediaQuery("@media (min-width: 380px)")

    buttonGroup {
        attrs.className = IDOL_COLOR_GRID_ACTIONS_CLASS_NAME
        attrs.classes(classes.root)

        button {
            attrs {
                variant = ButtonVariant.outlined
                size = ButtonSize.small
                disabled = props.selected.isEmpty()
                onClickFunction = { props.onClickPreview() }
                startIcon { icon { +"palette_icon" } }
            }

            if (showLabel) +"プレビュー"
        }

        button {
            attrs {
                variant = ButtonVariant.outlined
                size = ButtonSize.small
                disabled = props.selected.isEmpty()
                onClickFunction = { props.onClickPenlight() }
                startIcon { icon { +"highlight_icon" } }
            }

            if (showLabel) +"ペンライト"
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
    }
}

external interface IdolColorGridsActionsProps : RProps {
    var selected: List<IdolColor>
    var onClickPreview: () -> Unit
    var onClickPenlight: () -> Unit
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
