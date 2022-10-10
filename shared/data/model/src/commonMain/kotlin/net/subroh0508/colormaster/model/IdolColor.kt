package net.subroh0508.colormaster.model

data class IdolColor(
    val id: String,
    val name: String,
    val intColor: IntColor,
) {
    val isBrighter get() = intColor.isBrighter

    val color get() = intColor.toHex()
}

typealias IntColor = Triple<Int, Int, Int>

val IntColor.isBrighter get() = let { (red, green, blue) ->
    186 < (red * 0.299 + green * 0.587 + blue * 0.114)
}

fun IntColor.toHex() = buildString {
    append("#")
    this@toHex.toList().forEach {
        append(it.toString(16).padStart(2, '0').uppercase())
    }
}
