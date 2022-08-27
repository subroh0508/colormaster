package components.templates.search.frontlayer

import androidx.compose.runtime.Composable
import components.atoms.alert.Alert
import components.atoms.alert.AlertType
import kotlinx.js.jso
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import utilities.I18nextText
import utilities.LocalI18n
import utilities.invoke

@Composable
fun LoadStateAlert(
    params: SearchParams?,
    loadState: LoadState,
) {
    val t = LocalI18n() ?: return

    Alert(alertType(params, loadState), label(t, params, loadState))
}

private fun alertType(
    params: SearchParams?,
    loadState: LoadState,
): AlertType {
    if (params?.isEmpty() != false) {
        return AlertType.Info
    }

    return when (loadState) {
        is LoadState.Loaded<*> -> AlertType.Success
        is LoadState.Error -> AlertType.Error
        else -> AlertType.Warning
    }
}

@Composable
private fun label(
    t: I18nextText,
    params: SearchParams?,
    loadState: LoadState,
): String {
    if (loadState is LoadState.Loading) {
        return t("searchPanel.messages.searching")
    }

    if (loadState is LoadState.Error) {
        return t("searchPanel.messages.error")
    }

    val items: List<IdolColor> = loadState.getValueOrNull() ?: listOf()

    return when (params) {
        is SearchParams.ByName -> labelByName(t, params, items)
        is SearchParams.ByLive -> labelByLive(t, params, items)
        else -> t("searchPanel.messages.defaultByName")
    }
}

@Composable
private fun labelByLive(
    t: I18nextText,
    params: SearchParams.ByLive,
    items: List<IdolColor>,
) = t(
    when {
        params.isEmpty() -> t("searchPanel.messages.defaultByLive")
        else -> t("searchPanel.messages.success", count = items.size)
    }
)

@Composable
private fun labelByName(
    t: I18nextText,
    params: SearchParams.ByName,
    items: List<IdolColor>,
) = t(
    when {
        params.isEmpty() -> t("searchPanel.messages.defaultByName")
        else -> t("searchPanel.messages.success", count = items.size)
    }
)

private operator fun I18nextText.invoke(key: String, count: Int) = invoke(
    key,
    jso { asDynamic()["count"] = count },
)
