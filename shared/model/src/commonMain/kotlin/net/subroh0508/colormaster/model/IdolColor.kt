package net.subroh0508.colormaster.model

import net.subroh0508.colormaster.model.HexColor

data class IdolColor(
    val id: String,
    val name: String,
    private val hexColor: HexColor
) {
    val isBrighter: Boolean get() = hexColor.isBrighter

    val color = "#${hexColor.value}"
}
