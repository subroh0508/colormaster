package components.organisms.box.suggestions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableState
import material.components.Chip
import net.subroh0508.colormaster.model.LiveName
import net.subroh0508.colormaster.presentation.search.model.LiveNameQuery
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div
import usecase.rememberSearchLiveUseCase

@Composable
fun LiveSuggestList(
    state: MutableState<LiveNameQuery>,
    onSelect: (LiveName?) -> Unit,
) {
    val liveLoadState by rememberSearchLiveUseCase(state.value)

    val liveNames: List<LiveName> = liveLoadState.getValueOrNull() ?: listOf()

    LiveNameChips(liveNames, onSelect)
}

@Composable
private fun LiveNameChips(
    liveNames: List<LiveName>,
    onSelect: (LiveName?) -> Unit,
) {
    Style(LiveNameChipsStyle)

    Div({ classes(LiveNameChipsStyle.group) }) {
        liveNames.forEach {
            Chip(
                it.value,
                attrsScope = { classes(LiveNameChipsStyle.chip) },
            ) { onSelect(it) }
        }
    }
}

private object LiveNameChipsStyle : StyleSheet() {
    val group by style {
        display(DisplayStyle.Flex)
        flexWrap(FlexWrap.Wrap)
        padding(8.px, 16.px)
        gap(8.px, 8.px)
    }

    val chip by style {
        height(auto)
        paddingTop(4.px)
        paddingBottom(4.px)

        desc(self, className("mdc-evolution-chip__text-label")) style {
            whiteSpace("normal")
        }
    }
}
