package components.organisms.box.suggestions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableState
import components.atoms.chip.ChipGroup
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import usecase.rememberSearchLiveUseCase

@Composable
fun LiveSuggestList(
    state: MutableState<SearchParams>,
    onSelect: (LiveName?) -> Unit,
) {
    val params = state.value as? SearchParams.ByLive ?: return
    val liveLoadState by rememberSearchLiveUseCase(params)

    val liveNames: List<LiveName> = liveLoadState.getValueOrNull() ?: listOf()

    ChipGroup(
        liveNames.map(LiveName::value),
        null,
        { style { padding(8.px, 16.px) } },
    ) { onSelect(it?.let(::LiveName)) }
}
