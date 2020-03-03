package net.subroh0508.colormaster.domain.valueobject

data class IdolColor(
    val id: String,
    val name: String,
    private val hexColor: HexColor
) {
    val isBrighter: Boolean get() = hexColor.isBrighter

    val color = "#${hexColor.value}"
}
