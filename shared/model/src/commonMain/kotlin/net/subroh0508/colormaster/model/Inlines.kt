package net.subroh0508.colormaster.model

inline class IdolName(val value: String)

inline class HexColor(val value: String) {
    val isBrighter: Boolean get() {
        val red = value.substring(0, 2).toInt(16)
        val green = value.substring(2, 4).toInt(16)
        val blue = value.substring(4, 6).toInt(16)

        return 186 < (red * 0.299 + green * 0.587 + blue * 0.114)
    }
}
