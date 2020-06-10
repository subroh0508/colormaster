package net.subroh0508.colormaster.model

enum class Languages(val code: String, val label: String) {
    JAPANESE("ja", "日本語"),
    ENGLISH("en", "ENGLISH");

    operator fun component1() = code
    operator fun component2() = label

    companion object {
        fun valueOfCode(code: String) = values().find { it.code == code }
    }
}
