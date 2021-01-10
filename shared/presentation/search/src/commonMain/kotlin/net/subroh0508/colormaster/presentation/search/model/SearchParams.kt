package net.subroh0508.colormaster.presentation.search.model

import net.subroh0508.colormaster.model.Brands
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.model.UnitName

sealed class SearchParams {
    abstract fun isEmpty(): Boolean

    data class ByName(
        val idolName: IdolName?,
        val brands: Brands?,
        val types: Set<Types>,
        val unitName: UnitName?,
    ) : SearchParams() {
        override fun isEmpty() = this == EMPTY

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
            val EMPTY = ByName(null, null, setOf(), null)
        }
    }
}
