package net.subroh0508.colormaster.presentation.search.model

import net.subroh0508.colormaster.model.IdolColor

data class IdolColorList(
    val items: List<IdolColor> = listOf(),
    val inCharges: List<String> = listOf(),
    val favorites: List<String> = listOf(),
) : List<IdolColor> by items {
    fun inCharge(id: String) = inCharges.contains(id)
    fun favorite(id: String) = favorites.contains(id)
}
