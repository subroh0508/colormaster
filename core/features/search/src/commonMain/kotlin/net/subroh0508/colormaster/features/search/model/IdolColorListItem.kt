package net.subroh0508.colormaster.features.search.model

import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.IntColor

data class IdolColorListItem internal constructor(
    val id: String,
    val name: IdolName,
    val intColor: IntColor,
    val selected: Boolean = false,
    val inCharge: Boolean = false,
    val favorite: Boolean = false,
) {
    constructor(
        idolColor: IdolColor,
        selected: Boolean = false,
        inCharge: Boolean = false,
        favorite: Boolean = false,
    ) : this(
        idolColor.id,
        IdolName(idolColor.name),
        idolColor.intColor,
        selected,
        inCharge,
        favorite,
    )

    fun selected(selected: Boolean) = copy(selected = selected)
}


