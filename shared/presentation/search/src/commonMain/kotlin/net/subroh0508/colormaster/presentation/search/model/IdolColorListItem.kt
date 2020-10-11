package net.subroh0508.colormaster.presentation.search.model

import net.subroh0508.colormaster.model.HexColor
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName

data class IdolColorListItem internal constructor(
    val id: String,
    val name: IdolName,
    val hexColor: HexColor,
    val selected: Boolean = false,
) {
    constructor(
        idolColor: IdolColor,
        selected: Boolean = false,
    ) : this(
        idolColor.id,
        IdolName(idolColor.name),
        idolColor.hexColor,
        selected,
    )

    fun selected(selected: Boolean) = copy(selected = selected)
}


