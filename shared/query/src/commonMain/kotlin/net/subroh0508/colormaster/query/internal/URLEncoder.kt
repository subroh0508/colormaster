package net.subroh0508.colormaster.query.internal

internal expect object URLEncoder {
    fun encode(s: String): String
}
