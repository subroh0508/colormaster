package net.subroh0508.colormaster.model.ui.idol

import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.Titles
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.model.UnitName

sealed class Filters {
    abstract val idolName: IdolName?
    abstract val types: Set<Types>
    abstract val unitName: UnitName?

    object Empty : Filters() {
        override val idolName: IdolName? = null
        override val types: Set<Types> = setOf()
        override val unitName: UnitName? = null
    }

    data class NoTitle(
        override val idolName: IdolName?
    ) : Filters() {
        override val types: Set<Types> = setOf()
        override val unitName: UnitName? = null
    }

    data class _765AS(
        override val idolName: IdolName?,
        override val types: Set<Types.MILLION_LIVE>,
        override val unitName: UnitName?
    ) : Filters()

    data class _MillionStars(
        override val idolName: IdolName?,
        override val types: Set<Types.MILLION_LIVE>,
        override val unitName: UnitName?
    ) : Filters()

    data class _CinderellaGirls(
        override val idolName: IdolName?,
        override val types: Set<Types.CINDERELLA_GIRLS>,
        override val unitName: UnitName?
    ) : Filters()

    data class _ShinyColors(
        override val idolName: IdolName?,
        override val unitName: UnitName?
    ) : Filters() {
        override val types: Set<Types> = setOf()
    }

    data class _SideM(
        override val idolName: IdolName?,
        override val types: Set<Types.SIDE_M>,
        override val unitName: UnitName?
    ) : Filters()

    data class _876Pro(
        override val idolName: IdolName?
    ) : Filters() {
        override val types: Set<Types> = setOf()
        override val unitName: UnitName? = null
    }

    val title: Titles? get() = when (this) {
        is _765AS -> Titles._765
        is _MillionStars -> Titles._ML
        is _CinderellaGirls -> Titles._CG
        is _ShinyColors -> Titles._SC
        is _SideM -> Titles._315
        is _876Pro -> Titles._876
        else -> null
    }

    val allTypes: Set<Types> get() = when (this) {
        is _765AS -> Types.MILLION_LIVE.values().toSet()
        is _MillionStars -> Types.MILLION_LIVE.values().toSet()
        is _CinderellaGirls -> Types.CINDERELLA_GIRLS.values().toSet()
        is _SideM -> Types.SIDE_M.values().toSet()
        else -> setOf()
    }

    operator fun plus(type: Types) = when (this) {
        is _765AS -> if (type is Types.MILLION_LIVE) copy(types = types.toMutableSet().apply { add(type) }) else this
        is _MillionStars -> if (type is Types.MILLION_LIVE) copy(types = types.toMutableSet().apply { add(type) }) else this
        is _CinderellaGirls -> if (type is Types.CINDERELLA_GIRLS) copy(types = types.toMutableSet().apply { add(type) }) else this
        is _SideM -> if (type is Types.SIDE_M) copy(types = types.toMutableSet().apply { add(type) }) else this
        else -> this
    }

    operator fun minus(type: Types) = when (this) {
        is _765AS -> if (type is Types.MILLION_LIVE) copy(types = types.toMutableSet().apply { remove(type) }) else this
        is _MillionStars -> if (type is Types.MILLION_LIVE) copy(types = types.toMutableSet().apply { remove(type) }) else this
        is _CinderellaGirls -> if (type is Types.CINDERELLA_GIRLS) copy(types = types.toMutableSet().apply { remove(type) }) else this
        is _SideM -> if (type is Types.SIDE_M) copy(types = types.toMutableSet().apply { remove(type) }) else this
        else -> this
    }

    fun assign(unitName: UnitName? = null) = when (this) {
        is _765AS -> copy(unitName = unitName)
        is _MillionStars -> copy(unitName = unitName)
        is _CinderellaGirls -> copy(unitName = unitName)
        is _ShinyColors -> copy(unitName = unitName)
        is _SideM -> copy(unitName = unitName)
        else -> this
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        operator fun invoke(idolName: IdolName?, titles: Titles?, types: Set<Types> = emptySet()) = when (titles) {
            null -> NoTitle(idolName)
            Titles._765 -> _765AS(idolName, types as Set<Types.MILLION_LIVE>, null)
            Titles._ML -> _MillionStars(idolName, types as Set<Types.MILLION_LIVE>, null)
            Titles._CG -> _CinderellaGirls(idolName, types as Set<Types.CINDERELLA_GIRLS>, null)
            Titles._SC -> _ShinyColors(idolName, null)
            Titles._315 -> _SideM(idolName, types as Set<Types.SIDE_M>, null)
            Titles._876 -> _876Pro(idolName)
        }
    }
}
