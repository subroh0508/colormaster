package net.subroh0508.colormaster.model

abstract class UiModel {
    data class FullscreenPreview(
        val items: List<IdolColor>,
        val error: Throwable?,
        val isLoading: Boolean = false
    ) {
        companion object {
            val INITIALIZED = FullscreenPreview(emptyList(), null, true)
        }
    }
}
