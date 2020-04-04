package net.subroh0508.colormaster.repository

import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName

interface IdolColorsRepository {
    suspend fun rand(limit: Int): List<IdolColor>

    suspend fun search(name: IdolName?): List<IdolColor>
}
