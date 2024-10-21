package net.subroh0508.colormaster.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import net.subroh0508.colormaster.data.extension.search
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.PreviewRepository
import net.subroh0508.colormaster.network.imasparql.ImasparqlClient

internal class DefaultPreviewRepository(
    private val imasparqlClient: ImasparqlClient,
) : PreviewRepository {
    private val idolsStateFlow = MutableStateFlow<List<IdolColor>>(listOf())

    override fun getPreviewColorsStream(
        ids: List<String>,
        lang: String,
    ) = idolsStateFlow.onStart {
        idolsStateFlow.value = imasparqlClient.search(ids, lang)
    }
}
