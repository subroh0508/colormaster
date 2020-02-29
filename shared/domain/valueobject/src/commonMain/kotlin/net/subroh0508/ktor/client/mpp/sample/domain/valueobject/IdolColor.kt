package net.subroh0508.ktor.client.mpp.sample.domain.valueobject

data class IdolColor(
    val id: String,
    val name: String,
    private val hexColor: String
) {
    val isBrighter: Boolean get() {
        val red = hexColor.substring(0, 2).toInt(16)
        val green = hexColor.substring(2, 4).toInt(16)
        val blue = hexColor.substring(4, 6).toInt(16)

        return 186 < (red * 0.299 + green * 0.587 + blue * 0.114)
    }

    val color = "#$hexColor"
}
