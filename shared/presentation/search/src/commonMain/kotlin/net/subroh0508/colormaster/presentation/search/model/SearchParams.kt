package net.subroh0508.colormaster.presentation.search.model

import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.model.UnitName

data class SearchParams internal constructor(
    val idolName: IdolName?,
    val brands: Brands?,
    val types: Set<Types>,
    val unitName: UnitName?,
) {
    fun change(idolName: IdolName?) = copy(idolName = idolName)
    fun change(brands: Brands?) = copy(brands = brands, types = setOf(), unitName = null)
    fun change(type: Types, checked: Boolean) = copy(
        types = if (checked) addTypes(type) else removeTypes(type),
    )
    fun change(unitName: UnitName?) = if (brands != Brands._876) copy(unitName = unitName) else this

    private fun addTypes(type: Types) = when {
        (brands == Brands._ML || brands == Brands._765) && type is Types.MILLION_LIVE -> types + type
        brands == Brands._CG && type is Types.CINDERELLA_GIRLS -> types + type
        brands == Brands._315 && type is Types.SIDE_M -> types + type
        else -> types
    }
    private fun removeTypes(type: Types) = when {
        (brands == Brands._ML || brands == Brands._765) && type is Types.MILLION_LIVE -> types - type
        brands == Brands._CG && type is Types.CINDERELLA_GIRLS -> types - type
        brands == Brands._315 && type is Types.SIDE_M -> types - type
        else -> types
    }

    companion object {
        val EMPTY = SearchParams(null, null, setOf(), null)
    }
}
