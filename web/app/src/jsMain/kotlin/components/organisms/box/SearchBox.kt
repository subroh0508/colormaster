package components.organisms.box

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import components.atoms.textfield.OutlinedTextField
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement
import utilities.LocalI18n
import utilities.invoke

@Composable
fun SearchBox(type: SearchByTab) {
    val params = remember(type) {
        mutableStateOf(
            when (type) {
                SearchByTab.BY_NAME -> SearchParams.ByName.EMPTY
                SearchByTab.BY_LIVE -> SearchParams.ByLive.EMPTY
            }
        )
    }

    params.value.let { v ->
        when (v) {
            is SearchParams.ByName -> ByName(v) { params.value = it }
            is SearchParams.ByLive -> ByLive(v) { params.value = it }
        }
    }
}

private const val LABEL_BY_NAME = "search-by-name"
private const val LABEL_BY_LIVE = "search-by-live"

@Composable
private fun ByName(
    params: SearchParams.ByName,
    onChange: (SearchParams.ByName) -> Unit,
) {
    val t = LocalI18n() ?: return

    OutlinedTextField(
        LABEL_BY_NAME,
        t("searchBox.attributes.idolName"),
        params.idolName?.value,
        { style { padding(8.px, 16.px) } },
    ) { onChange(params.change(it.value.takeIf(String::isNotBlank)?.let(::IdolName))) }
}

@Composable
private fun ByLive(
    params: SearchParams.ByLive,
    onChange: (SearchParams.ByLive) -> Unit,
) {
    val t = LocalI18n() ?: return

    OutlinedTextField(
        LABEL_BY_LIVE,
        t("searchBox.attributes.liveName"),
        params.liveName?.value,
        { style { padding(8.px, 16.px) } },
    ) { onChange(params.change(it.value)) }
}
