package net.subroh0508.colormaster.repository

import net.subroh0508.colormaster.api.ImasparqlClient
import net.subroh0508.colormaster.domain.valueobject.IdolColor
import net.subroh0508.colormaster.domain.valueobject.IdolName
import net.subroh0508.colormaster.repository.mapper.toIdolColors

interface IdolColorsRepository {
    suspend fun search(name: IdolName): List<IdolColor>
}
