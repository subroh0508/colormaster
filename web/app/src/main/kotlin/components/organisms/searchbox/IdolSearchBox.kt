package components.organisms.searchbox

import kotlinx.css.pct
import kotlinx.css.width
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import react.*
import themes.APP_BAR_SM_UP

fun RBuilder.idolSearchBox(uiModel: SearchUiModel, handler: RHandler<IdolSearchBoxProps<*>>) = uiModel.params.let { params ->
    when (params) {
        is SearchParams.ByName -> child(SearchByNameComponent) { attrs.params = params; handler() }
        is SearchParams.ByLive -> child(SearchByLiveComponent) { attrs.params = params; handler() }
    }
}

const val DEBOUNCE_TIMEOUT_MILLS = 500L

external interface IdolSearchBoxProps<T: SearchParams> : RProps {
    var params: T
    var onChangeSearchParams: (SearchParams) -> Unit
}

external interface IdolSearchBoxStyle {
    val root: String
    val form: String
    val textField: String
}

val useStyle = makeStyles<IdolSearchBoxStyle> {
    "root" {
        (theme.breakpoints.up(Breakpoint.sm)) {
            width = APP_BAR_SM_UP
        }
    }
    "form" {
        width = 100.pct
    }
    "textField" {
        width = 100.pct
    }
}
