package components.templates

import components.organisms.FullscreenModalProps
import materialui.components.dialog.dialog
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogcontenttext.dialogContentText
import materialui.components.dialogtitle.dialogTitle
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import react.*

fun RBuilder.previewModal(handler: RHandler<PreviewModalProps>) = child(PreviewModalComponent, handler = handler)

external interface PreviewModalProps : RProps {
    var model: FullscreenPreviewUiModel
    @Suppress("PropertyName")
    var PreviewComponent: FunctionComponent<FullscreenModalProps>
}

private val PreviewModalComponent = functionComponent<PreviewModalProps> { props ->
    val (items, error, isLoading) = props.model

    when {
        isLoading -> loadingModal()
        error != null -> errorModal(error)
        else -> child(props.PreviewComponent) { attrs.items = items }
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
