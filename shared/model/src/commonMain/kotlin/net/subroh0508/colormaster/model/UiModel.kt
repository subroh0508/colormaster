package net.subroh0508.colormaster.model

abstract class UiModel {
    data class Search(
        val items: List<IdolColor>,
        val idolName: IdolName?,
        val error: Throwable?,
        val isLoading: Boolean = false
    ) {
        companion object {
            val INITIALIZED  = Search(emptyList(), null, null)
        }
    }
}
