package net.subroh0508.colormaster.presentation.preview.model

import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.common.LoadState

data class FullscreenPreviewUiModel(
    val items: List<IdolColor>,
    val error: Throwable? = null,
    val isLoading: Boolean = false,
) {
    companion object {
        val INITIALIZED = FullscreenPreviewUiModel(listOf())

        operator fun invoke(loadState: LoadState) = FullscreenPreviewUiModel(
            loadState.getValueOrNull<List<IdolColor>>() ?: listOf(),
            loadState.getErrorOrNull(),
            loadState.isLoading,
        )
    }
}
