package components.templates.search.frontlayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import components.atoms.backdrop.BackdropValues
import components.atoms.chip.OutlinedChip
import material.components.IconButton
import material.components.Tooltip
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import utilities.LocalI18n
import utilities.invoke

private const val ICON_PREVIEW = "palette"
private const val ICON_PENLIGHT = "highlight"
private const val ICON_ALL_SELECT = "check_box_outline_blank"
private const val ICON_ALL_DESELECT = "indeterminate_check_box"

private const val BUTTON_PREVIEW = "button-preview"
private const val BUTTON_PENLIGHT = "button-penlight"
private const val BUTTON_ALL_SELECT = "button-all-select"
private const val BUTTON_ALL_DESELECT = "button-all-deselect"

@Composable
fun ActionButtons(
    wide: Boolean,
    backdropState: State<BackdropValues>,
    selections: List<String>,
    onSelectAllClick: (Boolean) -> Unit,
) {
    if (!wide && backdropState.value == BackdropValues.Concealed) {
        return
    }

    val t = LocalI18n() ?: return
    val (selectId, selectKey, selectIcon) =
        if (selections.isNotEmpty())
            Triple(BUTTON_ALL_DESELECT, "actions.clear", ICON_ALL_DESELECT)
        else
            Triple(BUTTON_ALL_SELECT, "actions.all", ICON_ALL_SELECT)

    Style(ActionButtonsStyle)

    Div({ classes(ActionButtonsStyle.content) }) {
        if (!wide) {
            OutlinedChip(
                t("actions.preview"),
                disabled = selections.isEmpty(),
                leadingIcon = ICON_PREVIEW,
            )
            OutlinedChip(
                t("actions.penlight"),
                disabled = selections.isEmpty(),
                leadingIcon = ICON_PENLIGHT,
            )
            OutlinedChip(
                t(selectKey),
                leadingIcon = selectIcon,
                onClick = { onSelectAllClick(selections.isEmpty()) },
            )

            return@Div
        }

        IconButton(ICON_PREVIEW, applyAttrs = {
            attr("aria-describedby", BUTTON_PREVIEW)
            if (selections.isEmpty()) disabled()
        })
        Tooltip(BUTTON_PREVIEW, t("actions.preview"))

        IconButton(ICON_PENLIGHT, applyAttrs = {
            attr("aria-describedby", BUTTON_PENLIGHT)
            if (selections.isEmpty()) disabled()
        })
        Tooltip(BUTTON_PENLIGHT, t("actions.penlight"))

        IconButton(selectIcon, applyAttrs = {
            attr("aria-describedby", selectId)
            onClick { onSelectAllClick(selections.isEmpty()) }
        })
        Tooltip(selectId, t(selectKey))
    }
}

private object ActionButtonsStyle : StyleSheet() {
    val content by style {
        display(DisplayStyle.Flex)
        position(Position.Fixed)
        justifyContent(JustifyContent.FlexEnd)
        bottom(0.px)
        left(0.px)
        right(0.px)
        property("z-index", 6)
        padding(8.px, 4.px, 0.px, 0.px)
        backgroundColor(MaterialTheme.Var.surface)
        property("border-top", "1px solid ${MaterialTheme.Var.divider}")

        desc(self, className("mdc-evolution-chip")) style {
            margin(0.px, 4.px, 8.px, 0.px)
        }

        media(MEDIA_QUERY_TABLET_SMALL) {
            self style {
                justifyContent(JustifyContent.FlexStart)
                position(Position.Static)
                padding(0.px)
                marginBottom(8.px)
                backgroundColor(Color.transparent)
                property("border-top", "none")

                desc(self, className("mdc-icon-button")) style {
                    color(MaterialTheme.Var.onSurface)

                    (self + disabled) style {
                        color(MaterialTheme.Var.textDisabled)
                    }
                }
            }
        }
    }
}
