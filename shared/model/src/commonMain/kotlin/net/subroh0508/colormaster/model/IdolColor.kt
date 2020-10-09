package net.subroh0508.colormaster.model

data class IdolColor(
    val id: String,
    val name: String,
    val hexColor: HexColor
) {
    val isBrighter: Boolean get() = hexColor.isBrighter

    val color = "#${hexColor.value}"
}
