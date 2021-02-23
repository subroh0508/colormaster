package components.organisms.searchbox

import components.atoms.errorAlert
import components.atoms.infoAlert
import components.atoms.successAlert
import components.atoms.warningAlert
import kotlinext.js.jsObject
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import react.RBuilder
import react.RProps
import react.child
import react.functionalComponent
import utilities.I18nextText
import utilities.invoke
import utilities.useTranslation

fun RBuilder.message(opened: Boolean, uiModel: SearchUiModel) = when (uiModel) {
    is SearchUiModel.ByName -> child(MessageByNameComponent) { attrs.opened = opened; attrs.model = uiModel }
    is SearchUiModel.ByLive -> child(MessageByLiveComponent) { attrs.opened = opened; attrs.model = uiModel }
    is SearchUiModel.Favorites -> Unit
}

private val MessageByNameComponent = functionalComponent<MessageProps<SearchUiModel.ByName>> { props ->
    val (t, _) = useTranslation()

    when {
        props.model.isLoading -> warningAlert {
            attrs.message = t("searchPanel.messages.searching")
        }
        props.model.params.isEmpty() -> infoAlert {
            attrs.message = t("searchPanel.messages.default_by_name")
        }
        props.model.error != null -> errorAlert {
            attrs.title = t("searchPanel.messages.error")
            attrs.message = if (props.opened) props.model.error?.message ?: "" else ""
        }
        else -> successAlert {
            attrs.message = t("searchPanel.messages.success", count = props.model.items.size)
        }
    }
}

private val MessageByLiveComponent = functionalComponent<MessageProps<SearchUiModel.ByLive>> { props ->
    val (t, _) = useTranslation()

    val liveName = (props.model.params as? SearchParams.ByLive)?.liveName

    when {
        liveName == null -> infoAlert {
            attrs.message = t("searchPanel.messages.default_by_live")
        }
        props.model.isLoading -> warningAlert {
            attrs.message = t("searchPanel.messages.searching")
        }
        props.model.error != null -> errorAlert {
            attrs.title = t("searchPanel.messages.error")
            attrs.message = if (props.opened) props.model.error?.message ?: "" else ""
        }
        else -> successAlert {
            attrs.message = t("searchPanel.messages.success", count = props.model.items.size)
        }
    }
}

private operator fun I18nextText.invoke(key: String, count: Int) = invoke(
    key, jsObject { this.asDynamic()["count"] = count }
)

private external interface MessageProps<T: SearchUiModel> : RProps {
    var opened: Boolean
    var model: T
}
