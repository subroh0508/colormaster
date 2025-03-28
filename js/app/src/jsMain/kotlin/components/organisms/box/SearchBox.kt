package components.organisms.box

import MaterialTheme
import androidx.compose.runtime.*
import components.atoms.textfield.DebouncedTextForm
import components.atoms.textfield.OutlinedTextField
import components.organisms.box.form.BrandForm
import components.organisms.box.form.TypeForm
import components.organisms.box.suggestions.LiveSuggestList
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import material.components.TrailingTextFieldIcon
import net.subroh0508.colormaster.common.external.invoke
import net.subroh0508.colormaster.common.ui.LocalI18n
import net.subroh0508.colormaster.features.search.model.LiveNameQuery
import net.subroh0508.colormaster.features.search.model.SearchByTab
import net.subroh0508.colormaster.features.search.model.SearchParams
import net.subroh0508.colormaster.model.IdolName
import org.jetbrains.compose.web.css.*

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

    LaunchedEffect(type, onChange) {
        snapshotFlow { params.value }
            .onEach(onChange)
            .launchIn(this)
    }

    when (params.value) {
        is SearchParams.ByName -> @Suppress("UNCHECKED_CAST") ByName(params as MutableState<SearchParams.ByName>)
        is SearchParams.ByLive -> @Suppress("UNCHECKED_CAST") ByLive(params as MutableState<SearchParams.ByLive>)
        else -> Unit
    }
}

private const val DEBOUNCE_TIME_MILLIS = 500L

private const val LABEL_BY_NAME = "search-by-name"
private const val LABEL_BY_LIVE = "search-by-live"

@Composable
private fun ByName(state: MutableState<SearchParams.ByName>) {
    val t = LocalI18n() ?: return
    val params = state.value

    DebouncedTextForm(
        params.idolName?.value,
        DEBOUNCE_TIME_MILLIS,
        onDebouncedChange = { state.value = params.change(it?.let(::IdolName)) },
    ) { textState ->
        OutlinedTextField(
            LABEL_BY_NAME,
            t("searchBox.attributes.idolName"),
            textState.value,
            { style { padding(8.px, 16.px) } },
            onChange = { textState.value = it.value.takeIf(String::isNotBlank) },
        )
    }

    BrandForm(params.brands) { brand ->
        state.value = params.change(brand)
    }

    TypeForm(params.brands, params.types) { type, checked ->
        state.value = params.change(type, checked)
    }
}

@Composable
private fun ByLive(state: MutableState<SearchParams.ByLive>) {
    val t = LocalI18n() ?: return
    val liveNameQuery = remember { mutableStateOf(LiveNameQuery()) }

    LaunchedEffect(state) {
        snapshotFlow { liveNameQuery.value.toLiveName() }
            .onEach { liveName ->
                state.value = state.value.let {
                    if (liveName == null) it.clear() else it.select(liveName)
                }
            }
            .launchIn(this)
    }

    DebouncedTextForm(
        liveNameQuery.value.query,
        DEBOUNCE_TIME_MILLIS,
        onDebouncedChange = { liveNameQuery.value = liveNameQuery.value.change(it) },
    ) { textState ->
        OutlinedTextField(
            LABEL_BY_LIVE,
            t("searchBox.attributes.liveName"),
            textState.value,
            { style { padding(8.px, 16.px) } },
            disabled = liveNameQuery.value.isSettled,
            onChange = { textState.value = it.value.takeIf(String::isNotBlank) },
            trailing =
                if (liveNameQuery.value.isSettled) {
                    { ClearIcon(liveNameQuery) }
                } else
                    null,
        )
    }

    LiveSuggestList(liveNameQuery) { liveName ->
        liveNameQuery.value = liveNameQuery.value.let {
            if (liveName == null) it.clear() else it.settle(liveName)
        }
    }
}

@Composable
private fun ClearIcon(
    query: MutableState<LiveNameQuery>
) = TrailingTextFieldIcon(
    "highlight_off",
    {
        style {
            color(MaterialTheme.Var.textPrimary)
            property("pointer-events", "auto")
        }
    },
) { query.value = query.value.clear() }
