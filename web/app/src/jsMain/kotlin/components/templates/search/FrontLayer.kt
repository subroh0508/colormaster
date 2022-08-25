package components.templates.search

import MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import components.atoms.alert.Alert
import components.atoms.alert.AlertType
import components.atoms.backdrop.BackdropFrontHeader
import components.atoms.backdrop.BackdropValues
import components.organisms.list.SearchResultList
import material.components.Chip
import material.components.IconButton
import material.components.Tooltip
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import material.utilities.rememberMediaQuery
import net.subroh0508.colormaster.presentation.common.LoadState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import utilities.LocalI18n
import utilities.invoke

@Composable
fun FrontLayer(
    backdropState: MutableState<BackdropValues>,
    loadState: LoadState,
) {
    val t = LocalI18n() ?: return
    val wide by rememberMediaQuery(MEDIA_QUERY_TABLET_SMALL)

    console.log(loadState)

    BackdropFrontHeader(backdropState) {
        ActionButtonsForWide(wide)
        Alert(
            AlertType.Info,
            t("searchPanel.messages.defaultByName"),
        )
    }

    SearchResultList(loadState)
    ActionButtonsForNarrow(wide, backdropState)
}

private const val ICON_PREVIEW = "palette"
private const val ICON_PENLIGHT = "highlight"
private const val ICON_ALL_SELECT = "check_box_outline_blank"
private const val ICON_ALL_DESELECT = "indeterminate_check_box"

private const val BUTTON_PREVIEW = "button-preview"
private const val BUTTON_PENLIGHT = "button-penlight"
private const val BUTTON_ALL_SELECT = "button-all-select"
private const val BUTTON_ALL_DESELECT = "button-all-deselect"

@Composable
private fun ActionButtonsForWide(wide: Boolean) {
    if (!wide) {
        return
    }
    val t = LocalI18n() ?: return

    Style(ActionButtonsStyle)

    Div({ classes(ActionButtonsStyle.content) }) {
        IconButton(ICON_PREVIEW, applyAttrs = { attr("aria-describedby", BUTTON_PREVIEW) })
        Tooltip(BUTTON_PREVIEW, t("actions.preview"))

        IconButton(ICON_PENLIGHT, applyAttrs = { attr("aria-describedby", BUTTON_PENLIGHT) })
        Tooltip(BUTTON_PENLIGHT, t("actions.penlight"))

        IconButton(ICON_ALL_SELECT, applyAttrs = { attr("aria-describedby", BUTTON_ALL_SELECT) })
        Tooltip(BUTTON_ALL_SELECT, t("actions.all"))
    }
}

@Composable
private fun ActionButtonsForNarrow(wide: Boolean, backdropState: State<BackdropValues>) {
    if (wide || backdropState.value == BackdropValues.Concealed) {
        return
    }
    val t = LocalI18n() ?: return

    Style(ActionButtonsStyle)

    Div({ classes(ActionButtonsStyle.content) }) {
        Chip(t("actions.preview"), leadingIcon = ICON_PREVIEW)
        Chip(t("actions.penlight"), leadingIcon = ICON_PENLIGHT)
        Chip(t("actions.all"), leadingIcon = ICON_ALL_SELECT)
    }
}

private object ActionButtonsStyle : StyleSheet() {
    val content by style {
        display(DisplayStyle.Flex)
        position(Position.Absolute)
        justifyContent(JustifyContent.FlexEnd)
        bottom(0.px)
        left(0.px)
        right(0.px)
        paddingTop(8.px)
        backgroundColor(MaterialTheme.Var.surface)
        property("border-top", "1px solid ${MaterialTheme.Var.divider}")

        desc(self, className("mdc-evolution-chip")) style {
            margin(0.px, 8.px, 8.px, 0.px)

            desc(self, className("material-icons")) style {
                color(MaterialTheme.Var.onSurface)
            }
        }

        media(MEDIA_QUERY_TABLET_SMALL) {
            self style {
                justifyContent(JustifyContent.FlexStart)
                position(Position.Static)
                paddingTop(0.px)
                marginBottom(8.px)
                color(MaterialTheme.Var.onSurface)
                backgroundColor(Color.transparent)
                property("border-top", "none")
            }
        }
    }
}
