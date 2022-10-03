package net.subroh0508.colormaster.model

@Suppress("EnumEntryName")
enum class Brands(
    val displayName: String,
    val queryStr: String
) {
    _765("765PRO ALLSTARS","765AS"),
    _ML("MILLIONSTARS", "MillionLive"),
    _CG("CINDERELLA GIRLS", "CinderellaGirls"),
    _SC("SHINY COLORS", "ShinyColors"),
    _315("315 STARS", "SideM"),
    _876("876PRO", "DearlyStars");

    override fun toString() = displayName

    operator fun component1() = displayName
    operator fun component2() = queryStr
}
