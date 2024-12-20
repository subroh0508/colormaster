package net.subroh0508.colormaster.features.search.model

import net.subroh0508.colormaster.common.model.DateNum
import net.subroh0508.colormaster.model.*

sealed class SearchParams {
    abstract fun isEmpty(): Boolean

    data object None : SearchParams() { override fun isEmpty() = true }

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
            (brands == Brands._ML || brands == Brands._765) && type is Types.MillionLive -> types + type
            brands == Brands._CG && type is Types.CinderellaGirls -> types + type
            brands == Brands._315 && type is Types.SideM -> types + type
            else -> types
        }
        private fun removeTypes(type: Types) = when {
            (brands == Brands._ML || brands == Brands._765) && type is Types.MillionLive -> types - type
            brands == Brands._CG && type is Types.CinderellaGirls -> types - type
            brands == Brands._315 && type is Types.SideM -> types - type
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
            rawQuery == liveName?.value -> copy(query = null, date = null, suggests = listOf())
            rawQuery.isNullOrBlank() -> copy(liveName = null, query = null, date = null)
            isNumber(rawQuery) -> copy(date = DateNum(rawQuery.toInt()), query = null, liveName = null)
            else -> copy(query = rawQuery.takeIf(String::isNotBlank), date = null, liveName = null)
        }

        fun suggests(suggests: List<LiveName>) =
            if (liveName == suggests.firstOrNull() && suggests.size == 1)
                copy(suggests = listOf())
            else if (query == suggests.firstOrNull()?.value && suggests.size == 1)
                query.toLiveName()?.let(this@ByLive::select) ?: copy(suggests = suggests)
            else
                copy(suggests = suggests)

        fun select(liveName: LiveName) = EMPTY.copy(liveName = liveName)
        fun clear() = EMPTY

        private fun isNumber(rawQuery: String) = DATE_NUMBER_PATTERN.toRegex().matches(rawQuery)

        companion object {
            val EMPTY = ByLive(null, null, null, listOf())

            private const val DATE_NUMBER_PATTERN = """^[0-9]+$"""
        }
    }
}
