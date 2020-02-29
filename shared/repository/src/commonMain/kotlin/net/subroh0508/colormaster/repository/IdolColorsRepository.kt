package net.subroh0508.colormaster.repository

import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.repository.mapper.toIdolColors

class IdolColorsRepository(
    private val imasparqlClient: ImasparqlClient
) {
    suspend fun search() = imasparqlClient.search("dummy").toIdolColors()
}
