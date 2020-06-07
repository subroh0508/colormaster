enum class Languages(val code: String, val label: String, val basename: String) {
    JAPANESE("ja", "日本語", ""),
    ENGLISH("en", "ENGLISH", "/en");

    operator fun component1() = code
    operator fun component2() = label
    operator fun component3() = basename

    companion object {
        fun valueOfCode(code: String) = values().find { it.code == code }
    }
}
