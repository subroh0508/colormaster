package net.subroh0508.colormaster.model

import kotlinx.coroutines.flow.Flow

interface PreviewRepository {
    fun getPreviewColorsStream(ids: List<String>, lang: String): Flow<List<IdolColor>>
}
