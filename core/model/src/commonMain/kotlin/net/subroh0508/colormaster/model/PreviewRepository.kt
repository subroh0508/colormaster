package net.subroh0508.colormaster.model

import kotlinx.coroutines.flow.Flow

interface PreviewRepository {
    fun getPreviewColorsStream(): Flow<List<IdolColor>>

    fun clear()

    suspend fun show(ids: List<String>, lang: String)
}
