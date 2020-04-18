package components.templates

import components.atoms.fullscreenPenlight
import materialui.components.dialog.dialog
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogcontenttext.dialogContentText
import materialui.components.dialogtitle.dialogTitle
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.UiModel
import react.*

fun RBuilder.penlightModal(handler: RHandler<PenlightModalProps>) = child(PenlightModalComponent, handler = handler)

private val PenlightModalComponent = functionalComponent<PenlightModalProps> { props ->
    val (items, error, isLoading) = props.model

    when {
        isLoading -> loadingModal()
        error != null -> errorModal(error)
        else -> fullscreenPenlight { attrs.colors = items.map(IdolColor::color) }
    }
}

private fun RBuilder.loadingModal() = dialog {
    attrs.open = true

    dialogContent {
        dialogContentText(null) {
            +"読み込んでいます…"
        }
    }
}

private fun RBuilder.errorModal(error: Throwable) = dialog {
    attrs.open = true

    dialogTitle {
        +"エラー"
    }

    dialogContent {
        dialogContentText(null) {
            +(error.message ?: "エラーが発生しました")
        }
    }
}

external interface PenlightModalProps : RProps {
    var model: UiModel.FullscreenPreview
}
