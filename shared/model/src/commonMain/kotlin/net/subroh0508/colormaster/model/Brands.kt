package net.subroh0508.colormaster.model

@Suppress("EnumEntryName")
enum class Brands(
    val displayName: String,
    val queryStr: String
) {
    _765("765PRO ALLSTARS","765AS"),
    _ML("MILLIONSTARS", "MillionStars"),
    _CG("CINDERELLA GIRLS", "CinderellaGirls"),
    _SC("SHINY COLORS", "283Pro"),
    _315("315 STARS", "315ProIdols"),
    _876("876PRO", "DearlyStars");

    operator fun component1() = displayName
    operator fun component2() = queryStr
}
