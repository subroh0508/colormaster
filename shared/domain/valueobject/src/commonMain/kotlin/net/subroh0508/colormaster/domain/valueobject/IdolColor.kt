package net.subroh0508.colormaster.domain.valueobject

data class IdolColor internal constructor(
    val id: String,
    val name: String,
    private val hexColor: HexColor
) {
    constructor(id: String, name: String, hexColor: String) : this(id, name, HexColor(hexColor))

    val isBrighter: Boolean get() = hexColor.isBrighter

    val color = "#${hexColor.value}"
}
