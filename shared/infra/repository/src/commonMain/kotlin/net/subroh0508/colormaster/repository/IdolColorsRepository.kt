package net.subroh0508.colormaster.repository

import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.Titles
import net.subroh0508.colormaster.model.Types

interface IdolColorsRepository {
    suspend fun rand(limit: Int): List<IdolColor>

    suspend fun search(name: IdolName?, titles: Titles?, types: Set<Types>): List<IdolColor>
}
