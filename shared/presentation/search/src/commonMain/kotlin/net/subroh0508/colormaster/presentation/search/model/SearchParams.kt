package net.subroh0508.colormaster.presentation.search.model

import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.utilities.DateNum

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

    data class ByLive(
        val liveName: LiveName?,
        val query: String?,
        val date: DateNum?,
        val suggests: List<LiveName>,
    ) : SearchParams() {
        override fun isEmpty() = this == EMPTY

        fun change(rawQuery: String?) = when {
            rawQuery == null -> copy(liveName = null, query = null, date = null)
            isNumber(rawQuery) -> copy(date = DateNum(rawQuery.toInt()), query = null)
            else -> copy(query = rawQuery.takeIf(String::isNotBlank), date = null)
        }

        fun suggests(suggests: List<LiveName>) = copy(suggests = suggests)

        fun select(liveName: LiveName) = EMPTY.copy(liveName = liveName)
        fun clear() = EMPTY

        private fun isNumber(rawQuery: String) = DATE_NUMBER_PATTERN.toRegex().matches(rawQuery)

        companion object {
            val EMPTY = ByLive(null, null, null, listOf())

            private const val DATE_NUMBER_PATTERN = """^[0-9]+$"""
        }
    }
}
