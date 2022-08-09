package components.organisms.box

import androidx.compose.runtime.*
import components.atoms.textfield.OutlinedTextField
import components.organisms.box.form.BrandForm
import components.organisms.box.form.TypeForm
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.presentation.search.model.SearchByTab
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.jetbrains.compose.web.css.*
import utilities.LocalI18n
import utilities.invoke

@Composable
fun SearchBox(
    type: SearchByTab,
    onChange: (SearchParams) -> Unit,
) {
    val params = remember(type) {
        mutableStateOf(
            when (type) {
                SearchByTab.BY_NAME -> SearchParams.ByName.EMPTY
                SearchByTab.BY_LIVE -> SearchParams.ByLive.EMPTY
            },
        )
    }

    LaunchedEffect(onChange) {
        snapshotFlow { params.value }
            .onEach(onChange)
            .launchIn(this)
    }

    when (type) {
        SearchByTab.BY_NAME -> ByName(params)
        SearchByTab.BY_LIVE -> ByLive(params)
    }
}

private const val LABEL_BY_NAME = "search-by-name"
private const val LABEL_BY_LIVE = "search-by-live"

@Composable
private fun ByName(state: MutableState<SearchParams>) {
    val t = LocalI18n() ?: return
    val params = state.value as? SearchParams.ByName ?: return

    OutlinedTextField(
        LABEL_BY_NAME,
        t("searchBox.attributes.idolName"),
        params.idolName?.value,
        { style { padding(8.px, 16.px) } },
    ) { state.value = params.change(it.value.takeIf(String::isNotBlank)?.let(::IdolName)) }

    BrandForm(params.brands) { brand ->
        state.value = params.change(brand)
    }

    TypeForm(params.brands, params.types) { type, checked ->
        state.value = params.change(type, checked)
    }
}

@Composable
private fun ByLive(state: MutableState<SearchParams>) {
    val t = LocalI18n() ?: return
    val params = state.value as? SearchParams.ByLive ?: return

    OutlinedTextField(
        LABEL_BY_LIVE,
        t("searchBox.attributes.liveName"),
        params.liveName?.value,
        { style { padding(8.px, 16.px) } },
    ) { state.value = params.change(it.value) }
}
