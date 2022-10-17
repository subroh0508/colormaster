package components.templates.myidols

import MaterialTheme
import androidx.compose.runtime.Composable
import components.atoms.card.OutlinedCard
import components.organisms.list.FavoriteIdolsList
import components.organisms.list.InChargeIdolsList
import components.templates.search.frontlayer.*
import material.components.CardHeader
import material.components.Icon
import material.components.TypographyHeadline5
import net.subroh0508.colormaster.components.core.external.I18nextText
import net.subroh0508.colormaster.components.core.external.invoke
import net.subroh0508.colormaster.components.core.ui.LocalI18n
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import routes.CurrentLocalRouter

@Composable
fun InChargeIdolsCard(isSignedIn: Boolean) = MyIdolsCard(
    header = { t ->
        MyIdolsCardHeader("star", t("myPage.myIdols.inCharges"))
    },
    content = {
        InChargeIdolsList(isSignedIn) { selections, isSelectAllEnabled, setSelectionAll ->
            ActionButtons(selections, isSelectAllEnabled, setSelectionAll)
        }
    },
)

@Composable
fun FavoriteIdolsCard(isSignedIn: Boolean) = MyIdolsCard(
    header = { t ->
        MyIdolsCardHeader("favorite", t("myPage.myIdols.favorites"))
    },
    content = {
        FavoriteIdolsList(isSignedIn) { selections, isSelectAllEnabled, setSelectionAll ->
            ActionButtons(selections, isSelectAllEnabled, setSelectionAll)
        }
    },
)

@Composable
private fun MyIdolsCard(
    header: @Composable (I18nextText) -> Unit,
    content: @Composable (I18nextText) -> Unit,
) {
    val t = LocalI18n() ?: return

    OutlinedCard {
        header(t)
        content(t)
    }
}

@Composable
private fun MyIdolsCardHeader(icon: String, title: String) = CardHeader {
    TypographyHeadline5(applyAttrs = {
        style {
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            color(MaterialTheme.Var.onSurface)
        }
    }) {
        Icon(icon) { style { marginRight(4.px) } }
        Text(title)
    }
}

@Composable
private fun ActionButtons(
    selections: List<String>,
    isSelectAllEnabled: Boolean,
    setSelectionsAll: (Boolean) -> Unit
) {
    val router = CurrentLocalRouter() ?: return

    Style(IconActionButtonsStyle)

    Div({ classes(IconActionButtonsStyle.content) }) {
        IconActionButtons(
            selections,
            isSelectAllEnabled,
            setSelectionsAll,
            onOpenPreviewClick = { router.toPreview(selections) },
            onOpenPenlightClick = { router.toPenlight(selections) },
        )
    }
}

private object IconActionButtonsStyle : StyleSheet() {
    val content by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.FlexEnd)

        desc(self, className("mdc-icon-button")) style {
            color(MaterialTheme.Var.onSurface)

            (self + disabled) style {
                color(MaterialTheme.Var.textDisabled)
            }
        }
    }
}
